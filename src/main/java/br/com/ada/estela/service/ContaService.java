package br.com.ada.estela.service;

import br.com.ada.estela.enums.TipoConta;
import br.com.ada.estela.enums.TipoTransacao;
import br.com.ada.estela.exception.UnprocessableEntityException;
import br.com.ada.estela.mappers.ContaMapper;
import br.com.ada.estela.mappers.TransacaoMapper;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.model.Conta;
import br.com.ada.estela.model.Saldo;
import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.resource.conta.ContaDTO;
import br.com.ada.estela.resource.transacao.TransacaoDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class ContaService {
    public ContaDTO cadastrar(ContaDTO contaDTO) {

        Conta conta = new Conta();
        conta.setTipo(contaDTO.getTipo());
        conta.setNumero(gerarNumeroConta(contaDTO.getTipo()));
        Cliente cliente = Cliente.findById(contaDTO.getTitular().getId());
        if (cliente == null) {
            throw new NotFoundException("Cliente com id " + contaDTO.getTitular().getId() + " nao encontrado");
        }
        conta.setCliente(cliente);
        conta.persist();
        return ContaMapper.toDTO(conta, null, null);

    }

    public ContaDTO buscarPorId(Long id, UriInfo uriInfo) {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime fim = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);

        List<Transacao> transacoes = Transacao.find(
                "(contaOrigem.id = ?1 or contaDestino.id = ?1) and dataHora >= ?2 and dataHora <= ?3",
                id, inicio, fim).list();

        return ContaMapper.toDTO(Conta.findById(id), transacoes, uriInfo);

    }

    public String gerarNumeroConta(TipoConta tipo) {
        long sequencial = proximoSequencial();
        return String.format("%04d-%d", sequencial, tipo.getDigito());
    }

    private long proximoSequencial() {
        Conta ultima = Conta.find("ORDER BY numero DESC").firstResult();

        if (ultima == null) return 1;

        return Long.parseLong(ultima.getNumero().split("-")[0]) + 1;
    }

    @Transactional
    public TransacaoDTO depositar(Long id, TransacaoDTO transacaoDTO) {
        return realizarOperacao(id, transacaoDTO, TipoTransacao.DEPOSITO);
    }

    @Transactional
    public TransacaoDTO sacar(Long id, TransacaoDTO transacaoDTO) {
        return realizarOperacao(id, transacaoDTO, TipoTransacao.SAQUE);
    }

    @Transactional
    public TransacaoDTO transferir(Long id, TransacaoDTO transacaoDTO) {
        return realizarOperacao(id, transacaoDTO, TipoTransacao.TRANSFERENCIA);
    }

    private TransacaoDTO realizarOperacao(Long id, TransacaoDTO transacaoDTO, TipoTransacao tipoTransacao) {

        Conta conta = Conta.findById(id);
        if (conta == null) {
            throw new NotFoundException("Conta com id " + id + " nao encontrada");
        }

        if ((TipoTransacao.DEPOSITO == tipoTransacao || TipoTransacao.SAQUE == tipoTransacao) &&
                conta.getTipo() == TipoConta.ELETRONICA) {
            throw new UnprocessableEntityException("Conta do tipo ELETRONICA não permite saques/depósitos.");
        }
        Transacao transacao = new Transacao();
        transacao.setTipo(tipoTransacao);
        switch (tipoTransacao) {
            case DEPOSITO -> transacao.setContaDestino(conta);
            case SAQUE -> {
                verificarSaldoSuficiente(conta, transacaoDTO.getValor());
                transacao.setContaOrigem(conta);
            }
            case TRANSFERENCIA -> {
                verificarSaldoSuficiente(conta, transacaoDTO.getValor());
                transacao.setContaOrigem(conta);
                Conta contaDestino = Conta.find("id", transacaoDTO.getContaDestino().getId()).firstResult();
                if (contaDestino == null) {
                    throw new NotFoundException("Conta de destino com id " +
                            transacaoDTO.getContaDestino().getId() + " nao encontrada");
                }
                transacao.setContaDestino(contaDestino);
            }
            default -> throw new UnprocessableEntityException("Tipo de transação não suportada");
        }

        transacao.setValor(transacaoDTO.getValor());
        transacao.setDataHora(LocalDateTime.now());
        transacao.persistAndFlush();
        TransacaoDTO novaTransacaoDTO = TransacaoMapper.toDTO(transacao);
        return novaTransacaoDTO;
    }

    private void verificarSaldoSuficiente(Conta conta, BigDecimal valor) {
        Saldo saldo = Saldo.find("numero", conta.getNumero()).firstResult();
        if (saldo == null || saldo.getSaldo().compareTo(valor) < 0) {
            throw new UnprocessableEntityException("Saldo insuficiente para realizar saque/transferência.");
        }
    }

    public BigDecimal getSaldoAtual(Long contaId) {
        Conta conta = Conta.findById(contaId);
        if (conta == null) {
            throw new NotFoundException("Conta com id " + contaId + " nao encontrada");
        }
        Saldo saldo = Saldo.find("numero", conta.getNumero()).firstResult();
        return saldo != null ? saldo.getSaldo() : BigDecimal.ZERO;
    }
}
