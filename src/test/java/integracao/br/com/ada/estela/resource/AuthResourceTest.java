package br.com.ada.estela.resource;

import br.com.ada.estela.resource.auth.AuthResponse;
import br.com.ada.estela.service.AuthService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotAuthorizedException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@QuarkusTest
public class AuthResourceTest {

    @InjectMock
    private AuthService authService;

    @Test
    public void testLoginGerenteComCredenciaisValidas() {
        when(authService.autenticar("alice.silva@bancada.com.br", "senha123"))
                .thenReturn(new AuthResponse("token-gerente"));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "alice.silva@bancada.com.br", "senha", "senha123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    public void testLoginClienteComCredenciaisValidas() {
        when(authService.autenticar("carlos.oliveira@bancada.com.br", "senha123"))
                .thenReturn(new AuthResponse("token-cliente"));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "carlos.oliveira@bancada.com.br", "senha", "senha123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test
    public void testLoginComSenhaInvalida() {
        when(authService.autenticar("alice.silva@bancada.com.br", "senhaInvalida"))
                .thenThrow(new NotAuthorizedException("Credenciais invalidas"));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "alice.silva@bancada.com.br", "senha", "senhaInvalida"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void testLoginComUsuarioInexistente() {
        when(authService.autenticar("usuario.inexistente@bancada.com.br", "senha123"))
                .thenThrow(new NotAuthorizedException("Credenciais invalidas"));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "usuario.inexistente@bancada.com.br", "senha", "senha123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void testLoginSemCredenciais() {
        when(authService.autenticar(null, null))
                .thenThrow(new NotAuthorizedException("Credenciais invalidas"));

        given()
                .contentType(ContentType.JSON)
                .body(Map.of())
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void testLoginQuandoServicoRetornaNull() {
        when(authService.autenticar("sem.resposta@bancada.com.br", "senha123"))
                .thenReturn(null);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "sem.resposta@bancada.com.br", "senha", "senha123"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }
}
