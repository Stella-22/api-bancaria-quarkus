package br.com.ada.estela.enums;

public enum TipoConta {
    CORRENTE(1),
    POUPANCA(2),
    ELETRONICA(3);

    private final int digito;

    TipoConta(int digito) {
        this.digito = digito;
    }

    public int getDigito() {
        return digito;
    }
}
