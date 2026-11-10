package com.vonlanplace.doacao.recebedor.residencia;

import jakarta.persistence.EnumType;

/**
 * Tipo construtivo da moradia.
 *
 * <p>Campo declarado em {@link Residencia}, gravado por
 * {@link EnumType @Enumerated(EnumType.STRING)} — o valor persistido é o nome da
 * constante, não a descrição. Renomear uma constante quebraria os dados já gravados,
 * já que o projeto não tem migração.</p>
 */
public enum TipoResidencia {
    /** Construção de alvenaria convencional. */
    ALVENARIA_TRADICIONAL("Alvenaria Tradicional"),
    /** Alvenaria com estrutura reforçada. */
    ALVENARIA_ESTRUTURAL("Alvenaria Estrutural"),
    /** Estrutura de madeira. */
    MADEIRA("Madeira"),
    /** Drywall. */
    DRYWALL("Drywall"),
    /** Estrutura metálica. */
    METALICA("Estrutura Metálica"),
    /** Bioconstrução em taipa ou adobe. */
    BIOCONSTRUCAO("Bioconstrução (Taipa/Adobe)");


    /** Descrição legível, usada apenas em interface. */
    private final String descricao;

    /**
     * Construtor do enum.
     *
     * @param descricao descrição legível
     */
    TipoResidencia(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Devolve a descrição legível do tipo de moradia.
     *
     * @return texto exibível
     */
    public String getDescricao() {
        return descricao;
    }
}
