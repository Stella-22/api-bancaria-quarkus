package br.com.ada.estela.mappers;

import br.com.ada.estela.model.Transacao;
import br.com.ada.estela.resource.transacao.TransacaoDTO;

import java.util.List;

public class TransacaoMapper {

    public static TransacaoDTO toDTO(Transacao transacao) {
        if (transacao == null) {
            return null;
        }
        TransacaoDTO dto = new TransacaoDTO();
        dto.setId(transacao.getId());
        dto.setValor(transacao.getValor());
        dto.setTipo(transacao.getTipo());
        dto.setDataHora(transacao.getDataHora());
        return dto;
    }

    public static Transacao toEntity(TransacaoDTO dto) {
        if (dto == null) {
            return null;
        }
        Transacao transacao = new Transacao();
        transacao.setId(dto.getId());
        transacao.setValor(dto.getValor());
        transacao.setTipo(dto.getTipo());
        transacao.setDataHora(dto.getDataHora());
        return transacao;
    }

    public static List<TransacaoDTO> toDTO(List<Transacao> transacoes) {
        if (transacoes == null) {
            return null;
        }
        return transacoes.stream()
                .map(TransacaoMapper::toDTO)
                .toList();
    }

        public static List<Transacao> toEntity(List<TransacaoDTO> dtos) {
            if (dtos == null) {
                return null;
            }
            return dtos.stream()
                    .map(TransacaoMapper::toEntity)
                    .toList();
        }

}
