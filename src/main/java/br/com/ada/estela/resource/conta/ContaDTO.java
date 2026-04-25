package br.com.ada.estela.resource.conta;

import br.com.ada.estela.enums.TipoConta;
import br.com.ada.estela.resource.Link;
import br.com.ada.estela.resource.cliente.ClienteDTO;
import br.com.ada.estela.resource.transacao.TransacaoDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContaDTO {

    private Long id;

    @NotNull(message = "O tipo da conta é obrigatório")
    private TipoConta tipo;

    @NotNull(message = "O titular é obrigatório")
    private ClienteDTO titular;

    private String numero;

    private BigDecimal saldo;

    private List<TransacaoDTO> transacoes;

    @JsonProperty("_links")
    private Link links;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public void setTipo(TipoConta tipo) {
        this.tipo = tipo;
    }

    public ClienteDTO getTitular() {
        return titular;
    }

    public void setTitular(ClienteDTO titular) {
        this.titular = titular;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public List<TransacaoDTO> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<TransacaoDTO> transacoes) {
        this.transacoes = transacoes;
    }

    public Link getLinks() {
        return links;
    }

    public void setLinks(Link links) {
        this.links = links;
    }
}
