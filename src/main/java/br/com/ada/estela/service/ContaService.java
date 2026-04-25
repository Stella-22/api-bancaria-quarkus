package br.com.ada.estela.service;

import br.com.ada.estela.mappers.ContaMapper;
import br.com.ada.estela.model.Cliente;
import br.com.ada.estela.model.Conta;
import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.resource.conta.ContaDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.UriInfo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@ApplicationScoped
public class ContaService {
    public ContaDTO cadastrar(@Valid ContaDTO contaDTO) {

        Conta conta = new Conta();
        conta.setTipo(contaDTO.getTipo());
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
        List<Transacao> transacoes = Transacao.find("dataHora >= ?1 and dataHora <= ?2", inicio, fim).list();
        return ContaMapper.toDTO(Cliente.findById(id), transacoes, uriInfo);
    }
}
