package br.com.ada.estela.service;

import br.com.ada.estela.mappers.TransacaoMapper;
import br.com.ada.estela.model.Conta;
import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.repository.ContaRepository;
import br.com.ada.estela.repository.TransacaoRepository;
import br.com.ada.estela.resource.transacao.TransacaoDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.util.List;


@ApplicationScoped
public class TransacaoService {

    @Inject
    private TransacaoRepository transacaoRepository;

    @Inject
    private ContaRepository contaRepository;

    public TransacaoDTO buscarPorId(Long id) {
        Transacao transacao = transacaoRepository.find("id = ?1", id).firstResult();
        return TransacaoMapper.toDTO(transacao);
    }

    public List<TransacaoDTO> buscarPorContaId(Long contaId) {
        Conta conta = contaRepository.findById(contaId);
        if (conta == null) {
            throw new NotFoundException("Conta com id " + contaId + " nao encontrada");
        }
        List<Transacao> transacoes = transacaoRepository.find(
                "contaOrigem.id = ?1 or contaDestino.id = ?1", contaId).list();
        return TransacaoMapper.toDTO(transacoes);
    }
}
