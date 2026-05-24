package br.com.ada.estela.resource;

import br.com.ada.estela.enums.PerfilUsuario;
import br.com.ada.estela.enums.TipoConta;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.model.Conta;
import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.repository.ClienteRepository;
import br.com.ada.estela.repository.ContaRepository;
import br.com.ada.estela.repository.TransacaoRepository;
import br.com.ada.estela.resource.conta.ContaDTO;
import br.com.ada.estela.resource.cliente.ClienteDTO;
import br.com.ada.estela.resource.transacao.TransacaoDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ContaResourceTest {

    @Inject
    private ClienteRepository clienteRepository;

    @Inject
    private ContaRepository contaRepository;

    @Inject
    private TransacaoRepository transacaoRepository;

    private Long idCliente;
    private Long idContaExistente;
    private Long idContaDestino;

    // Tokens obtidos via /auth/login
    private String tokenGerente;
    private String tokenCliente;

    @BeforeEach
    @Transactional
    public void setup() {
        final String emailCliente = "carlos.oliveira@bancada.com.br";
        final String senhaPadrao = "senha123";

        // Buscar cliente de teste
        idCliente = buscarCliente(emailCliente).getId();

        // Buscar contas de teste
        idContaExistente = buscarConta("0001-1").getId();
        idContaDestino = buscarConta("0002-2").getId();

        // Buscar tokens
        tokenGerente = loginAndGetToken("alice.silva@bancada.com.br", senhaPadrao);
        tokenCliente = loginAndGetToken(emailCliente, senhaPadrao);
    }

    @Test
    public void testCadastrarContaValida() {
        ContaDTO contaDTO = new ContaDTO();
        contaDTO.setTipo(TipoConta.CORRENTE);

        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(idCliente);
        contaDTO.setTitular(clienteDTO);

        // Deve criar conta com status 201 e id gerado (usuário GERENTE)
        given()
                .auth().oauth2(tokenGerente)
                .contentType(ContentType.JSON)
                .body(contaDTO)
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("numero", notNullValue())
                .body("tipo", equalTo("CORRENTE"));
    }

    @Test
    public void testBuscarContaExistente() {
        // Deve buscar conta com status 200 e campos corretos (pode ser cliente)
        given()
                .auth().oauth2(tokenCliente)
                .when()
                .get("/contas/" + idContaExistente)
                .then()
                .statusCode(200)
                .body("id", equalTo(idContaExistente.intValue()))
                .body("tipo", notNullValue())
                .body("numero", notNullValue())
                .body("titular", notNullValue());
    }

    @Test
    public void testBuscarContaInexistente() {
        // Deve retornar 404 para conta inexistente
        given()
                .auth().oauth2(tokenCliente)
                .when()
                .get("/contas/99999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDepositarValorValido() {
        TransacaoDTO transacaoDTO = new TransacaoDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(100.00));

        // Deve depositar com status 200 e saldo atualizado no body
        given()
                .auth().oauth2(tokenCliente)
                .contentType(ContentType.JSON)
                .body(transacaoDTO)
                .when()
                .post("/contas/" + idContaExistente + "/deposito")
                .then()
                .statusCode(200)
                .body("valor", equalTo(100.00f))
                .body("saldoAtual", notNullValue())
                .body("saldoAtual", greaterThan(0f));
    }

    @Test
    public void testSacarComSaldoSuficiente() {
        TransacaoDTO transacaoDTO = new TransacaoDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(100.00));

        // Deve sacar com status 200 e saldo atualizado
        given()
                .auth().oauth2(tokenCliente)
                .contentType(ContentType.JSON)
                .body(transacaoDTO)
                .when()
                .post("/contas/" + idContaExistente + "/saque")
                .then()
                .statusCode(200)
                .body("valor", equalTo(100.00f))
                .body("saldoAtual", notNullValue());
    }

    @Test
    public void testSacarSemSaldo() {
        TransacaoDTO transacaoDTO = new TransacaoDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(5000.00));

        // Deve retornar 422 (Unprocessable Entity)
        given()
                .auth().oauth2(tokenCliente)
                .contentType(ContentType.JSON)
                .body(transacaoDTO)
                .when()
                .post("/contas/" + idContaExistente + "/saque")
                .then()
                .statusCode(422)
                .body("erro", containsStringIgnoringCase("saldo"));
    }

    @Test
    public void testTransferenciaComSaldoSuficiente() {
        TransacaoDTO transacaoDTO = new TransacaoDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(100.00));

        ContaDTO contaDestinoDTO = new ContaDTO();
        contaDestinoDTO.setId(idContaDestino);
        transacaoDTO.setContaDestino(contaDestinoDTO);

        // Deve transferir com status 200 e saldo atualizado no body
        given()
                .auth().oauth2(tokenCliente)
                .contentType(ContentType.JSON)
                .body(transacaoDTO)
                .when()
                .post("/contas/" + idContaExistente + "/transferencia")
                .then()
                .statusCode(200)
                .body("valor", equalTo(100.00f))
                .body("saldoAtual", notNullValue());
    }

    @Test
    public void testTransferenciaSemSaldo() {
        TransacaoDTO transacaoDTO = new TransacaoDTO();
        transacaoDTO.setValor(BigDecimal.valueOf(5000.00));

        ContaDTO contaDestinoDTO = new ContaDTO();
        contaDestinoDTO.setId(idContaDestino);
        transacaoDTO.setContaDestino(contaDestinoDTO);

        // Deve retornar 422 (Unprocessable Entity)
        given()
                .auth().oauth2(tokenCliente)
                .contentType(ContentType.JSON)
                .body(transacaoDTO)
                .when()
                .post("/contas/" + idContaExistente + "/transferencia")
                .then()
                .statusCode(422)
                .body("erro", containsStringIgnoringCase("saldo"));
    }

    // ===== Métodos auxiliares =====

    private Cliente buscarCliente(String email) {
        return clienteRepository.find("email", email).firstResult();
    }

    private Conta buscarConta(String numero) {
        return contaRepository.find("numero", numero).firstResult();
    }

    // Helper para login e extrair token JWT do response
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