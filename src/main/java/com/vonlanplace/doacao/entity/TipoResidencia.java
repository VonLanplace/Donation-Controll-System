package com.vonlanplace.doacao.entity;

public enum TipoResidencia {
    ALVENARIA_TRADICIONAL("Alvenaria Tradicional"),
    ALVENARIA_ESTRUTURAL("Alvenaria Estrutural"),
    MADEIRA("Madeira"),
    DRYWALL("Drywall"),
    METALICA("Estrutura Metálica"),
    BIOCONSTRUCAO("Bioconstrução (Taipa/Adobe)");


    private final String descricao;

    TipoResidencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
