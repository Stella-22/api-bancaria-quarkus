package br.com.ada.estela.service;

import br.com.ada.estela.enums.TipoConta;
import br.com.ada.estela.enums.TipoTransacao;
import br.com.ada.estela.exception.UnprocessableEntityException;
import br.com.ada.estela.mappers.ContaMapper;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.model.Conta;
import br.com.ada.estela.model.Saldo;
import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.repository.ClienteRepository;
import br.com.ada.estela.repository.ContaRepository;
import br.com.ada.estela.repository.SaldoRepository;
import br.com.ada.estela.repository.TransacaoRepository;
import br.com.ada.estela.resource.cliente.ClienteDTO;
import br.com.ada.estela.resource.conta.ContaDTO;
import br.com.ada.estela.resource.transacao.TransacaoDTO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    private static final Long ID_CONTA = 1L;

    private static final Long ID_CONTA_DESTINO = 2L;

    private static final String MSG_CONTA_ELETRONICA_SEM_SAQUE_DEPOSITO =
            "Conta do tipo ELETRONICA não permite saques/depósitos.";

    private static final String MSG_SALDO_INSUFICIENTE =
            "Saldo insuficiente";

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private SaldoRepository saldoRepository;

    @InjectMocks
    private ContaService contaService;

    @Test
    public void deveCadastrarContaCorrente() {
        // Padrão "AAA" de testes
        // Arrange/Preparação
        ContaDTO dto = criarContaDTO(TipoConta.CORRENTE);
        preparacaoCadastrarConta();

        // Act/Execução
        ContaDTO contaCriada = contaService.cadastrar(dto);

        // Assert/Verificação
        verify(contaRepository, times(1)).persist(any(Conta.class));
        assertNotNull(contaCriada);
        assertEquals(TipoConta.CORRENTE, contaCriada.getTipo());
        assertEquals("0001-1", contaCriada.getNumero());
    }

    @Test
    public void deveCadastrarContaPoupanca() {
        // Padrão "AAA" de testes
        // Arrange/Preparação
        ContaDTO dto = criarContaDTO(TipoConta.POUPANCA);
        preparacaoCadastrarConta();

        // Act/Execução
        ContaDTO contaCriada = contaService.cadastrar(dto);

        // Assert/Verificação
        verify(contaRepository, times(1)).persist(any(Conta.class));
        assertNotNull(contaCriada);
        assertEquals(TipoConta.POUPANCA, contaCriada.getTipo());
        assertEquals("0001-2", contaCriada.getNumero());
    }

    @Test
    public void deveCadastrarContaEletronica() {
        // Padrão "AAA" de testes
        // Arrange/Preparação
        ContaDTO dto = criarContaDTO(TipoConta.ELETRONICA);
        preparacaoCadastrarConta();

        // Act/Execução
        ContaDTO contaCriada = contaService.cadastrar(dto);

        // Assert/Verificação
        verify(contaRepository, times(1)).persist(any(Conta.class));
        assertNotNull(contaCriada);
        assertEquals(TipoConta.ELETRONICA, contaCriada.getTipo());
        assertEquals("0001-3", contaCriada.getNumero());
    }

    @Test
    public void naoDeveCadastrarContaComClienteInexistente() {
        // Arrange/Preparação
        ContaDTO dto = criarContaDTO(TipoConta.CORRENTE);
        PanacheQuery<Conta> queryMock = Mockito.mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(null);
        when(contaRepository.find(anyString())).thenReturn(queryMock);
        when(clienteRepository.findById(anyLong())).thenReturn(null);

        // Act/Execução & Assert/Verificação
        assertThrows(NotFoundException.class, () -> contaService.cadastrar(dto));
    }

    @Test
    public void deveBuscarContaPorId() {
        // Arrange/Preparação
        UriInfo uriInfoMock = criarUriInfo();

        when(transacaoRepository.find(anyString(), anyLong(), any(), any())).thenReturn(Mockito.mock(PanacheQuery.class));
        when(contaRepository.findById(ID_CONTA)).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.CORRENTE)));

        // Act/Execução
        ContaDTO contaConsultada = contaService.buscarPorId(ID_CONTA, uriInfoMock);

        // Assert/Verificação
        assertNotNull(contaConsultada);
        assertEquals(TipoConta.CORRENTE, contaConsultada.getTipo());
    }

    @Test
    public void deveRealizarDepositoConta() {
        // Arrange/Preparação
        TransacaoDTO dto = criarTransacaoDTO(158.00);

        when(contaRepository.findById(anyLong())).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.CORRENTE)));
        doNothing().when(transacaoRepository).persistAndFlush(any());

        // Act/Execução
        TransacaoDTO transacaoCriada = contaService.depositar(ID_CONTA, dto);

        // Assert/Verificação
        verify(transacaoRepository, times(1)).persistAndFlush(any(Transacao.class));
        assertNotNull(transacaoCriada);
        assertEquals(BigDecimal.valueOf(158.00), transacaoCriada.getValor());
        assertEquals(TipoTransacao.DEPOSITO, transacaoCriada.getTipo());
        assertNotNull(transacaoCriada.getContaDestino());
    }

    @Test
    public void naoDeveRealizarDepositoContaInexistente() {
        // Arrange/Preparação
        TransacaoDTO dto = criarTransacaoDTO(158.00);

        when(contaRepository.findById(anyLong())).thenReturn(null);

        // Act/Execução & Assert/Verificação
        assertThrows(NotFoundException.class, () -> contaService.depositar(ID_CONTA, dto));
    }

    @Test
    public void naoDeveRealizarDepositoContaEletronica() {
        // Arrange/Preparação
        TransacaoDTO dto = criarTransacaoDTO(158.00);

        when(contaRepository.findById(anyLong())).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.ELETRONICA)));

        // Act/Execução & Assert/Verificação
        UnprocessableEntityException ex = assertThrows(UnprocessableEntityException.class, () -> contaService.depositar(ID_CONTA, dto));
        assertEquals(MSG_CONTA_ELETRONICA_SEM_SAQUE_DEPOSITO, ex.getMessage());
    }

    @Test
    public void deveRealizarSaqueConta() {
        // Arrange/Preparação
        TransacaoDTO dto = criarTransacaoDTO(158.00);

        when(contaRepository.findById(anyLong())).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.CORRENTE)));

        PanacheQuery<Saldo> queryMock = Mockito.mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(criarSaldo(200.00));
        when(saldoRepository.find(anyString(), anyString())).thenReturn(queryMock);
        doNothing().when(transacaoRepository).persistAndFlush(any());

        // Act/Execução
        TransacaoDTO transacaoCriada = contaService.sacar(ID_CONTA, dto);

        // Assert/Verificação
        verify(transacaoRepository, times(1)).persistAndFlush(any(Transacao.class));
        assertNotNull(transacaoCriada);
        assertEquals(BigDecimal.valueOf(158.00), transacaoCriada.getValor());
        assertEquals(TipoTransacao.SAQUE, transacaoCriada.getTipo());
        assertNotNull(transacaoCriada.getConta());
    }

    @Test
    public void naoDeveRealizarSaqueContaEletronica() {
        // Arrange/Preparação
        TransacaoDTO dto = criarTransacaoDTO(158.00);

        when(contaRepository.findById(anyLong())).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.ELETRONICA)));

        // Act/Execução & Assert/Verificação
        UnprocessableEntityException ex = assertThrows(UnprocessableEntityException.class, () -> contaService.sacar(ID_CONTA, dto));
        assertEquals(MSG_CONTA_ELETRONICA_SEM_SAQUE_DEPOSITO, ex.getMessage());
    }

    @Test
    public void naoDeveRealizarSaqueContaComSaldoInsuficiente() {
        // Arrange/Preparação
        TransacaoDTO dto = criarTransacaoDTO(158.00);

        when(contaRepository.findById(anyLong())).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.CORRENTE)));

        PanacheQuery<Saldo> queryMock = Mockito.mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(criarSaldo(100.00));
        when(saldoRepository.find(anyString(), anyString())).thenReturn(queryMock);

        // Act/Execução & Assert/Verificação
        UnprocessableEntityException ex = assertThrows(UnprocessableEntityException.class, () -> contaService.sacar(ID_CONTA, dto));
        assertTrue(ex.getMessage().contains(MSG_SALDO_INSUFICIENTE));
    }

    @Test
    public void deveRealizarTransferenciaContas() {
        // Arrange/Preparação
        TransacaoDTO dto = criarTransacaoDTO(158.00);
        ContaDTO destino = criarContaDTO(TipoConta.CORRENTE);
        destino.setId(2L);
        dto.setContaDestino(destino);

        when(contaRepository.findById(ID_CONTA)).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.CORRENTE)));
        PanacheQuery<Saldo> queryMock = Mockito.mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(criarSaldo(200.00));
        when(saldoRepository.find(anyString(), anyString())).thenReturn(queryMock);

        when(contaRepository.findById(ID_CONTA_DESTINO)).thenReturn(ContaMapper.toEntity(destino));
        doNothing().when(transacaoRepository).persistAndFlush(any());

        // Act/Execução
        TransacaoDTO transacaoCriada = contaService.transferir(ID_CONTA, dto);

        // Assert/Verificação
        verify(transacaoRepository, times(1)).persistAndFlush(any(Transacao.class));
        assertNotNull(transacaoCriada);
        assertEquals(BigDecimal.valueOf(158.00), transacaoCriada.getValor());
        assertEquals(TipoTransacao.TRANSFERENCIA, transacaoCriada.getTipo());
        assertNotNull(transacaoCriada.getConta());
        assertNotNull(transacaoCriada.getContaDestino());
    }

    @Test
    public void naoDeveRealizarTransferenciaSaldoInsuficiente() {
        // Arrange/Preparação
        TransacaoDTO dto = criarTransacaoDTO(158.00);

        when(contaRepository.findById(anyLong())).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.CORRENTE)));

        PanacheQuery<Saldo> queryMock = Mockito.mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(criarSaldo(100.00));
        when(saldoRepository.find(anyString(), anyString())).thenReturn(queryMock);

        // Act/Execução & Assert/Verificação
        UnprocessableEntityException ex = assertThrows(UnprocessableEntityException.class, () -> contaService.transferir(ID_CONTA, dto));
        assertTrue(ex.getMessage().contains(MSG_SALDO_INSUFICIENTE));
    }

    @Test
    public void naoDeveRealizarTransferenciaContaDestinoInexistente() {
        // Arrange/Preparação
        TransacaoDTO dto = criarTransacaoDTO(158.00);
        ContaDTO destino = criarContaDTO(TipoConta.CORRENTE);
        destino.setId(2L);
        dto.setContaDestino(destino);

        when(contaRepository.findById(ID_CONTA)).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.CORRENTE)));
        PanacheQuery<Saldo> queryMock = Mockito.mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(criarSaldo(200.00));
        when(saldoRepository.find(anyString(), anyString())).thenReturn(queryMock);

        when(contaRepository.findById(ID_CONTA_DESTINO)).thenReturn(null);

        // Act/Execução & Assert/Verificação
        NotFoundException ex = assertThrows(NotFoundException.class, () -> contaService.transferir(ID_CONTA, dto));
        assertTrue(ex.getMessage().contains("Conta de destino"));
    }

    @Test
    public void deveConsultaSaldoAtualConta() {
        // Arrange/Preparação
        when(contaRepository.findById(ID_CONTA)).thenReturn(ContaMapper.toEntity(criarContaDTO(TipoConta.CORRENTE)));

        PanacheQuery<Saldo> queryMock = Mockito.mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(criarSaldo(200.00));
        when(saldoRepository.find(anyString(), anyString())).thenReturn(queryMock);

        // Act/Execução
        BigDecimal saldo = contaService.getSaldoAtual(ID_CONTA);

        // Assert/Verificação
        assertNotNull(saldo);
        assertEquals(BigDecimal.valueOf(200.00), saldo);
    }

    @Test
    public void naoDeveConsultarSaldoContaInexistente() {
        // Arrange/Preparação
        when(contaRepository.findById(ID_CONTA)).thenReturn(null);

        // Act/Execução && Assert/Verificação
        assertThrows(NotFoundException.class, () -> contaService.getSaldoAtual(ID_CONTA));
    }

    private Saldo criarSaldo(double valor) {
        Saldo saldo = new Saldo();
        saldo.setSaldo(BigDecimal.valueOf(valor));
        return saldo;
    }

    private TransacaoDTO criarTransacaoDTO(double valor) {
        TransacaoDTO dto = new TransacaoDTO();
        dto.setValor(BigDecimal.valueOf(valor));
        return dto;
    }

    private void preparacaoCadastrarConta() {
        PanacheQuery<Conta> queryMock = Mockito.mock(PanacheQuery.class);
        when(queryMock.firstResult()).thenReturn(null);
        when(contaRepository.find(anyString())).thenReturn(queryMock);
        when(clienteRepository.findById(anyLong())).thenReturn(new Cliente());
        doNothing().when(contaRepository).persist(any(Conta.class));
    }

    private ContaDTO criarContaDTO(TipoConta tipo) {
        ContaDTO dto = new ContaDTO();
        dto.setId(ID_CONTA);
        dto.setNumero("0001-" + tipo.getDigito());
        dto.setTipo(tipo);
        dto.setTitular(criarClienteDTO());
        return dto;
    }

    private ClienteDTO criarClienteDTO() {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(1L);
        clienteDTO.setNome("João Silva");
        clienteDTO.setCpf("123.456.789-00");
        return clienteDTO;
    }

    private UriInfo criarUriInfo() {
        UriInfo uriInfoMock = Mockito.mock(UriInfo.class);
        UriBuilder uriBuilderMock = Mockito.mock(UriBuilder.class);
        when(uriInfoMock.getBaseUriBuilder()).thenReturn(uriBuilderMock);
        when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.queryParam(anyString(), any())).thenReturn(uriBuilderMock);
        when(uriBuilderMock.build()).thenReturn(java.net.URI.create("http://localhost/transacoes?contaId=1"));
        return uriInfoMock;
    }
}

