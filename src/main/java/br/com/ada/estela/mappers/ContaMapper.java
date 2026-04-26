package br.com.ada.estela.mappers;

import br.com.ada.estela.model.Conta;
import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.resource.Link;
import br.com.ada.estela.resource.conta.ContaDTO;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;

public class ContaMapper {
    public static ContaDTO toDTO(Conta conta, List<Transacao> transacoes, UriInfo uriInfo) {
        if (conta == null) {
            return null;
        }
        ContaDTO dto = new ContaDTO();
        dto.setId(conta.getId());
        dto.setNumero(conta.getNumero());
        dto.setTipo(conta.getTipo());
        dto.setTitular(ClienteMapper.toDTO(conta.getCliente()));
        if (transacoes != null) {
            dto.setTransacoes(TransacaoMapper.toDTO(transacoes));
            dto.setLinks(new Link("transacoes",
                    uriInfo.getBaseUriBuilder().path("transacoes").queryParam("contaId", conta.id).build().toString(),
                    "GET"));
        }
        return dto;
    }

    public static Conta toEntity(ContaDTO dto) {
        if (dto == null) {
            return null;
        }
        Conta conta = new Conta();
        conta.setId(dto.getId());
        conta.setNumero(dto.getNumero());
        conta.setTipo(dto.getTipo());
        conta.setCliente(ClienteMapper.toEntity(dto.getTitular()));
        return conta;
    }
}
