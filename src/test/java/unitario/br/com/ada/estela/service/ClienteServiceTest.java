package br.com.ada.estela.service;

import br.com.ada.estela.mappers.ClienteMapper;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.repository.ClienteRepository;
import br.com.ada.estela.resource.cliente.ClienteDTO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    public void deveCadastrarCliente() {
        // Arrange/Preparação
        ClienteDTO dto = criarClienteDTO();
        doNothing().when(clienteRepository).persist(any(Cliente.class));

        // Act/Execução
        ClienteDTO clienteCadastrado = clienteService.cadastrar(dto);

        // Assert/Verificação
        verify(clienteRepository, times(1)).persist(any(Cliente.class));
        assertNotNull(clienteCadastrado);
        assertEquals(dto.getNome(), clienteCadastrado.getNome());
        assertEquals(dto.getEmail(), clienteCadastrado.getEmail());
    }

    @Test
    public void deveBuscarTodosClientes() {
        // Arrange/Preparação
        PanacheQuery<Cliente> queryMock = Mockito.mock(PanacheQuery.class);
        when(clienteRepository.findAll()).thenReturn(queryMock);
        when(queryMock.stream()).thenReturn(Stream.of(new Cliente(), new Cliente()));

        // Act/Execução
        List<ClienteDTO> lista = clienteService.buscarTodos();

        // Assert/Verificação
        assertNotNull(lista);
        assertEquals(2, lista.size());
    }

    @Test
    public void deveBuscarClientePorId() {
        // Arrange/Preparação
        Cliente cliente = ClienteMapper.toEntity(criarClienteDTO());
        when(clienteRepository.findById(anyLong())).thenReturn(cliente);

        // Act/Execução
        ClienteDTO dto = clienteService.buscarPorId(1L);

        // Assert/Verificação
       assertNotNull(dto);
       assertEquals(cliente.getNome(), dto.getNome());
       assertEquals(cliente.getEmail(), dto.getEmail());
    }

    @Test
    public void deveAtualizarCliente() {
        // Arrange/Preparação
        Cliente cliente = ClienteMapper.toEntity(criarClienteDTO());
        when(clienteRepository.findById(anyLong())).thenReturn(cliente);
        doNothing().when(clienteRepository).persist(any(Cliente.class));

        // Act/Execução
        ClienteDTO dto = clienteService.atualizar(1L, criarClienteDTO());

        // Assert/Verificação
        verify(clienteRepository, times(1)).persist(any(Cliente.class));
        assertNotNull(dto);
        assertEquals(cliente.getNome(), dto.getNome());
        assertEquals(cliente.getEmail(), dto.getEmail());
    }

    @Test
    public void naoDeveAtualizarClienteInexistente() {
        // Arrange/Preparação
        when(clienteRepository.findById(anyLong())).thenReturn(null);

        // Act/Execução && Assert/Validação
        assertThrows(NotFoundException.class, () -> clienteService.atualizar(1L, criarClienteDTO()));
    }

    @Test
    public void naoDeveAtualizarCPFCliente() {
        // Arrange/Preparação
        Cliente cliente = ClienteMapper.toEntity(criarClienteDTO());
        cliente.setCpf("111.111.111-11");
        when(clienteRepository.findById(anyLong())).thenReturn(cliente);

        // Act/Execução && Assert/Validação
        BadRequestException ex = assertThrows(BadRequestException.class, () -> clienteService.atualizar(1L, criarClienteDTO()));
        assertTrue(ex.getMessage().contains("CPF nao pode ser alterado"));
    }

    private ClienteDTO criarClienteDTO() {
        ClienteDTO dto = new ClienteDTO();
        dto.setNome("João da Silva");
        dto.setCpf("123.456.789-00");
        dto.setEmail("jose.silva@email.com");
        dto.setSenha("teste123");
        return dto;
    }
}
