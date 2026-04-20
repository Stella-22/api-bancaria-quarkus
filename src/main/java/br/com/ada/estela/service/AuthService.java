package br.com.ada.estela.service;

import br.com.ada.estela.model.Usuario;
import br.com.ada.estela.resource.auth.AuthResponse;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Duration;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

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
        Usuario usuario = Usuario.find("email", email).firstResult();
        validarSenha(usuario,senha);
        String token = gerarToken(usuario);
        return new AuthResponse(token);
    }

    private void validarSenha(Usuario user, String password) {
        boolean approve = user != null
                && argon2.verify(user.getSenha(), password.toCharArray());

        if (!approve) {
            throw new NotAuthorizedException("Credenciais invalidas");
        }
    }

    private String gerarToken(Usuario usuario) {
        return Jwt.issuer(issuer)
                .upn(usuario.getEmail())
                .groups(usuario.getRole().name())
                .claim("usuarioId", usuario.id)
                .claim("nome", usuario.getNome())
                .expiresIn(Duration.ofMinutes(30))
                .sign();
    }
}

