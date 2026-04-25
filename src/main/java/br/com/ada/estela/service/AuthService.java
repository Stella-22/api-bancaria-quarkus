package br.com.ada.estela.service;

import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.resource.auth.AuthResponse;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.Duration;


@ApplicationScoped
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class);

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    private Argon2 argon2;

    @PostConstruct
    void init() {
        argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }

    public String hashPassword(String password) {
        return argon2.hash(2, 65536, 1, password.toCharArray());
    }

    public AuthResponse autenticar(String email, String senha) {

        LOG.infof("Tentativa de login para username='%s'", email);
        Cliente cliente = Cliente.find("email", email).firstResult();
        validarSenha(cliente,senha);
        String token = gerarToken(cliente);
        LOG.infof("Login bem-sucedido para username='%s'", email);
        return new AuthResponse(token);
    }

    private void validarSenha(Cliente cliente, String password) {
        boolean approve = cliente != null
                && argon2.verify(cliente.getSenha(), password.toCharArray());

        if (!approve) {
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

