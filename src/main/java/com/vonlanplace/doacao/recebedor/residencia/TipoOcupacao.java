package com.vonlanplace.doacao.recebedor.residencia;

public enum TipoOcupacao {
    PROPRIEDADE("Propriedade"),
    LOCACAO("Locação"),
    COMODATO("Comodato"),
    POSSE("Posse"),
    USUCAPIAO("Usucapião");

    private final String descricao;

    TipoOcupacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}