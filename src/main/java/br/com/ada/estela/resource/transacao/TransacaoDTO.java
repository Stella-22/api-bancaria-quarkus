package br.com.ada.estela.resource.transacao;

import br.com.ada.estela.enums.TipoTransacao;
import br.com.ada.estela.resource.conta.ContaDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransacaoDTO {

    private Long id;

    private TipoTransacao tipo;

    @NotNull(message = "O valor da transacao é obrigatório")
    private BigDecimal valor;

    private LocalDateTime dataHora;

    private ContaDTO conta;

    private ContaDTO contaDestino;

    private BigDecimal saldoAtual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public ContaDTO getConta() {
        return conta;
    }

    public void setConta(ContaDTO conta) {
        this.conta = conta;
    }

    public ContaDTO getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(ContaDTO contaDestino) {
        this.contaDestino = contaDestino;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }
}
