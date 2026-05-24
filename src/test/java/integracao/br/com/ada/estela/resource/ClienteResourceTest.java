package br.com.ada.estela.resource;

import br.com.ada.estela.enums.PerfilUsuario;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.repository.ClienteRepository;
import br.com.ada.estela.resource.cliente.ClienteDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class ClienteResourceTest {

    @Inject
    private ClienteRepository clienteRepository;

    private Long idClienteExistente;
    private Long idClienteParaAtualizar;
    private String cpfClienteParaAtualizar;
    private String tokenGerente;
    private String tokenCliente;

    @BeforeEach
    @Transactional
    public void setup() {
        Cliente cliente = buscarCliente("carlos.oliveira@bancada.com.br");
        idClienteExistente = cliente.getId();

        Cliente clienteParaAtualizar = criarClienteParaAtualizar();
        idClienteParaAtualizar = clienteParaAtualizar.getId();
        cpfClienteParaAtualizar = clienteParaAtualizar.getCpf();

        tokenGerente = loginAndGetToken("alice.silva@bancada.com.br", "senha123");
        tokenCliente = loginAndGetToken("carlos.oliveira@bancada.com.br", "senha123");
    }

    @Test
    public void testCadastrarClienteValido() {
        ClienteDTO clienteDTO = criarClienteDTO(
                "Mariana Costa",
                "111.222.333-44",
                "mariana.costa@bancada.com.br",
                "senha123");

        given()
                .auth().oauth2(tokenGerente)
                .contentType(ContentType.JSON)
                .body(clienteDTO)
                .when()
                .post("/clientes")
                .then()
                .statusCode(201)
                .header("Location", containsString("/clientes/"))
                .body("id", notNullValue())
                .body("nome", equalTo("Mariana Costa"))
                .body("email", equalTo("mariana.costa@bancada.com.br"));
    }

    @Test
    public void testBuscarClientes() {
        given()
                .auth().oauth2(tokenGerente)
                .when()
                .get("/clientes")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(4))
                .body("[0].id", notNullValue())
                .body("[0].nome", notNullValue())
                .body("[0].email", notNullValue());
    }

    @Test
    public void testBuscarClienteExistente() {
        given()
                .auth().oauth2(tokenGerente)
                .when()
                .get("/clientes/" + idClienteExistente)
                .then()
                .statusCode(200)
                .body("id", equalTo(idClienteExistente.intValue()))
                .body("nome", equalTo("Carlos Oliveira"))
                .body("email", equalTo("carlos.oliveira@bancada.com.br"));
    }

    @Test
    public void testBuscarClienteInexistente() {
        given()
                .auth().oauth2(tokenGerente)
                .when()
                .get("/clientes/99999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarClienteValido() {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Carlos Oliveira Atualizado");
        clienteDTO.setEmail("carlos.atualizado@bancada.com.br");
        clienteDTO.setCpf(cpfClienteParaAtualizar);
        clienteDTO.setSenha("novaSenha123");

        given()
                .auth().oauth2(tokenGerente)
                .contentType(ContentType.JSON)
                .body(clienteDTO)
                .when()
                .put("/clientes/" + idClienteParaAtualizar)
                .then()
                .statusCode(200)
                .body("id", equalTo(idClienteParaAtualizar.intValue()))
                .body("nome", equalTo("Carlos Oliveira Atualizado"))
                .body("email", equalTo("carlos.atualizado@bancada.com.br"));
    }

    @Test
    public void testAtualizarClienteInexistente() {
        ClienteDTO clienteDTO = criarClienteDTO(
                "Cliente Inexistente",
                "222.333.444-55",
                "cliente.inexistente@bancada.com.br",
                "senha123");

        given()
                .auth().oauth2(tokenGerente)
                .contentType(ContentType.JSON)
                .body(clienteDTO)
                .when()
                .put("/clientes/99999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarCpfClienteNaoPermitido() {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setCpf("999.999.999-99");

        given()
                .auth().oauth2(tokenGerente)
                .contentType(ContentType.JSON)
                .body(clienteDTO)
                .when()
                .put("/clientes/" + idClienteParaAtualizar)
                .then()
                .statusCode(400);
    }

    @Test
    public void testSemPerfilGerenteNaoPodeBuscarClientes() {
        given()
                .auth().oauth2(tokenCliente)
                .when()
                .get("/clientes")
                .then()
                .statusCode(403);
    }

    private Cliente buscarCliente(String email) {
        return clienteRepository.find("email", email).firstResult();
    }

    private Cliente criarClienteParaAtualizar() {
        long sufixo = System.nanoTime();
        String cpf = String.format("%03d.%03d.%03d-%02d",
                sufixo % 1000,
                (sufixo / 1000) % 1000,
                (sufixo / 1000000) % 1000,
                (sufixo / 1000000000) % 100);

        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Atualizacao");
        cliente.setCpf(cpf);
        cliente.setEmail("cliente.atualizacao." + sufixo + "@bancada.com.br");
        cliente.setSenha(BCrypt.hashpw("senha123", BCrypt.gensalt(10)));
        cliente.setRole(PerfilUsuario.CLIENTE);
        clienteRepository.persist(cliente);
        return cliente;
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

    private ClienteDTO criarClienteDTO(String nome, String cpf, String email, String senha) {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome(nome);
        clienteDTO.setCpf(cpf);
        clienteDTO.setEmail(email);
        clienteDTO.setSenha(senha);
        return clienteDTO;
    }
}
