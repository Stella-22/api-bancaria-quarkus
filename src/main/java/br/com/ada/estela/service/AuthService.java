package br.com.ada.estela.service;

import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.repository.ClienteRepository;
import br.com.ada.estela.resource.auth.AuthResponse;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.jboss.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.time.Duration;

@ApplicationScoped
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class);

    @Inject
    private ClienteRepository clienteRepository;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;


    public String hashPassword(String password) {

        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    public AuthResponse autenticar(String email, String senha) {
        LOG.infof("Tentativa de login para username='%s'", email);
        Cliente cliente = clienteRepository.find("email", email).firstResult();
        validarSenha(cliente, senha);
        String token = gerarToken(cliente);
        LOG.infof("Login bem-sucedido para username='%s'", email);
        return new AuthResponse(token);
    }

    private void validarSenha(Cliente cliente, String password) {
        // Valida se cliente existe e tem senha válida
        final String credenciaisInvalidas = "Credenciais inválidas";
        if (cliente == null || cliente.getSenha() == null || cliente.getSenha().isBlank()) {
            LOG.warn("Cliente ou senha não informados.");
            throw new NotAuthorizedException(credenciaisInvalidas);
        }

        try {
            boolean aprovado = BCrypt.checkpw(password, cliente.getSenha());
            if (!aprovado) {
                LOG.warn("Senhas não conferem.");
                throw new NotAuthorizedException(credenciaisInvalidas);
            }
        } catch (IllegalArgumentException e) {
            // Hash corrompido no banco de dados
            LOG.errorf("Hash de senha inválido para cliente: %s", cliente.getEmail());
            throw new NotAuthorizedException(credenciaisInvalidas);
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