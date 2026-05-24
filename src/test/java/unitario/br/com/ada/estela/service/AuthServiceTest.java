package br.com.ada.estela.service;

import br.com.ada.estela.enums.PerfilUsuario;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.repository.ClienteRepository;
import br.com.ada.estela.resource.auth.AuthResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.NotAuthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private static final String EMAIL = "usuario.teste@email.com";
    private static final String SENHA = "teste123";

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    public void deveEncriptarSenha() {
        // Arrange/Preparação

        // Act/Execução
        String senhaEncriptada = authService.hashPassword(SENHA);

        // Assert/Validação
        assertNotNull(senhaEncriptada);
        assertNotEquals(SENHA, senhaEncriptada);
    }

    @Test
    public void deveAutenticarUsuarioCliente() {
        // Arrange/Preparação
        PanacheQuery<Cliente> queryMock = mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(criarCliente());
        when(clienteRepository.find(anyString(), anyString())).thenReturn(queryMock);

        // Act/Execução
        AuthResponse authResponse = authService.autenticar(EMAIL, SENHA);

        // Assert/Validação
        assertNotNull(authResponse);
        assertNotNull(authResponse.token());
    }

    @Test
    public void naoDeveAutenticarUsuarioInexistente() {
        // Arrange/Preparação
        PanacheQuery<Cliente> queryMock = mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(null);
        when(clienteRepository.find(anyString(), anyString())).thenReturn(queryMock);

        // Act/Execução && Assert/Validação
        assertThrows(NotAuthorizedException.class, () -> authService.autenticar(EMAIL, SENHA));
    }

    @Test
    public void naoDeveAutenticarUsuarioSenhaNaoConfere() {
        // Arrange/Preparação
        Cliente cliente = criarCliente();
        cliente.setSenha("teste");

        PanacheQuery<Cliente> queryMock = mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(cliente);
        when(clienteRepository.find(anyString(), anyString())).thenReturn(queryMock);

        // Act/Execução && Assert/Validação
        assertThrows(NotAuthorizedException.class, () -> authService.autenticar(EMAIL, SENHA));

    }

    private Cliente criarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João da Silva");
        cliente.setEmail(EMAIL);
        cliente.setSenha(authService.hashPassword(SENHA));
        cliente.setRole(PerfilUsuario.CLIENTE);
        return cliente;
    }
}
