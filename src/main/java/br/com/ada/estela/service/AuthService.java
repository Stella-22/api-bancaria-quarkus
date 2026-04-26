package br.com.ada.estela.service;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.resource.auth.AuthResponse;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import org.jboss.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;

@ApplicationScoped
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class);

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    // Removidos: Argon2, @PostConstruct e init()

    public String hashPassword(String password) {
        // Gera hash compatível com pgcrypto bcrypt (fator 10)
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public AuthResponse autenticar(String email, String senha) {
        LOG.infof("Tentativa de login para username='%s'", email);
        Cliente cliente = Cliente.find("email", email).firstResult();
        validarSenha(cliente, senha);
        String token = gerarToken(cliente);
        LOG.infof("Login bem-sucedido para username='%s'", email);
        return new AuthResponse(token);
    }

    private void validarSenha(Cliente cliente, String password) {
        // BCrypt.checkpw verifica a senha contra o hash $2a$10$... do banco
        boolean aprovado = cliente != null
                && BCrypt.checkpw(password, cliente.getSenha());

        if (!aprovado) {
            throw new NotAuthorizedException("Credenciais invalidas");
        }
    }

    private String gerarToken(Cliente cliente) {
        return Jwt.issuer(issuer)
                .upn(cliente.getEmail())
                .groups(cliente.getRole().name())
                .claim("clienteId", cliente.id)
                .claim("nome", cliente.getNome())
                .expiresIn(Duration.ofMinutes(30))
                .sign();
    }
}