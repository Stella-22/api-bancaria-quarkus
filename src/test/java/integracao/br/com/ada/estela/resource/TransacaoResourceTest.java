package br.com.ada.estela.resource;

import br.com.ada.estela.enums.TipoTransacao;
import br.com.ada.estela.model.Conta;
import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.repository.ContaRepository;
import br.com.ada.estela.repository.TransacaoRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class TransacaoResourceTest {

    @Inject
    private ContaRepository contaRepository;

    @Inject
    private TransacaoRepository transacaoRepository;

    private Long idContaExistente;
    private Long idTransacaoExistente;
    private String tokenGerente;
    private String tokenCliente;

    @BeforeEach
    @Transactional
    public void setup() {
        Conta conta = buscarConta("0001-1");
        idContaExistente = conta.getId();

        Transacao transacao = buscarTransacao(TipoTransacao.DEPOSITO);
        idTransacaoExistente = transacao.getId();

        tokenGerente = loginAndGetToken("alice.silva@bancada.com.br", "senha123");
        tokenCliente = loginAndGetToken("carlos.oliveira@bancada.com.br", "senha123");
    }

    @Test
    public void testBuscarTransacaoExistente() {
        given()
                .auth().oauth2(tokenCliente)
                .when()
                .get("/transacoes/" + idTransacaoExistente)
                .then()
                .statusCode(200)
                .body("id", equalTo(idTransacaoExistente.intValue()))
                .body("tipo", equalTo("DEPOSITO"))
                .body("valor", notNullValue())
                .body("dataHora", notNullValue())
                .body("contaDestino", notNullValue());
    }

    @Test
    public void testBuscarTransacaoInexistente() {
        given()
                .auth().oauth2(tokenCliente)
                .when()
                .get("/transacoes/99999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testBuscarTransacoesPorContaExistente() {
        given()
                .auth().oauth2(tokenGerente)
                .queryParam("contaId", idContaExistente)
                .when()
                .get("/transacoes")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(3))
                .body("[0].id", notNullValue())
                .body("[0].tipo", notNullValue())
                .body("[0].valor", notNullValue())
                .body("[0].dataHora", notNullValue());
    }

    @Test
    public void testBuscarTransacoesPorContaInexistente() {
        given()
                .auth().oauth2(tokenGerente)
                .queryParam("contaId", 99999)
                .when()
                .get("/transacoes")
                .then()
                .statusCode(404);
    }

    @Test
    public void testBuscarTransacaoSemToken() {
        given()
                .when()
                .get("/transacoes/" + idTransacaoExistente)
                .then()
                .statusCode(401);
    }

    private Conta buscarConta(String numero) {
        return contaRepository.find("numero", numero).firstResult();
    }

    private Transacao buscarTransacao(TipoTransacao tipo) {
        return transacaoRepository.find("tipo", tipo).firstResult();
    }

    private String loginAndGetToken(String email, String senha) {
        return given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "senha", senha))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
