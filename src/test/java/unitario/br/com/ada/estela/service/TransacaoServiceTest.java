package br.com.ada.estela.service;

import br.com.ada.estela.enums.TipoTransacao;
import br.com.ada.estela.model.Conta;
import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.repository.ContaRepository;
import br.com.ada.estela.repository.TransacaoRepository;
import br.com.ada.estela.resource.transacao.TransacaoDTO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    @Test
    public void deveBuscarTransacaoPorId() {
        // Arrange/Verificação
        PanacheQuery queryMock = Mockito.mock(PanacheQuery.class);
        when(transacaoRepository.find(anyString(), anyLong())).thenReturn(queryMock);
        when(queryMock.firstResult()).thenReturn(criarTransacao());

        // Act/Execução
        TransacaoDTO dto = transacaoService.buscarPorId(1L);

        // Assert/Verificação
        assertNotNull(dto);
        assertEquals(TipoTransacao.DEPOSITO, dto.getTipo());
        assertEquals(new BigDecimal("100.00"), dto.getValor());
        assertNotNull(dto.getDataHora());
        assertNotNull(dto.getConta());
        assertNotNull(dto.getContaDestino());
    }

    @Test
    public void deveBuscarTransacoesPorIdConta() {
        // Arrange/Verificação
        when(contaRepository.findById(anyLong())).thenReturn(new Conta());
        PanacheQuery queryMock = Mockito.mock(PanacheQuery.class);
        when(transacaoRepository.find(anyString(), anyLong())).thenReturn(queryMock);
        when(queryMock.list()).thenReturn(List.of(criarTransacao()));

        // Act/Execução
        List<TransacaoDTO> transacoes = transacaoService.buscarPorContaId(1L);

        // Assert/Verificação
        assertNotNull(transacoes);
        assertEquals(1, transacoes.size());
    }

    @Test
    public void naoDeveBuscarTransacoesContaInexistente() {
        // Arrange/Verificação
        when(contaRepository.findById(anyLong())).thenReturn(null);

        // Act/Execução && // Assert/Verificação
        assertThrows(NotFoundException.class, () -> transacaoService.buscarPorContaId(1L));
    }

    private Transacao criarTransacao() {
        Transacao transacao = new Transacao();
        transacao.setTipo(TipoTransacao.DEPOSITO);
        transacao.setDataHora(now());
        transacao.setValor(new BigDecimal("100.00"));
        transacao.setContaOrigem(new Conta());
        transacao.setContaDestino(new Conta());
        return transacao;
    }
}
