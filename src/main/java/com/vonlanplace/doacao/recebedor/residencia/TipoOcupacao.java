package com.vonlanplace.doacao.recebedor.residencia;

import jakarta.persistence.EnumType;

/**
 * Forma de ocupação do imóvel em que o recebedor mora.
 *
 * <p>Campo do tipo ocupação declarado em {@link Residencia}, gravado por
 * {@link EnumType @Enumerated(EnumType.STRING)} — o valor persistido é o nome da
 * constante, não a descrição. Renomear uma constante quebraria os dados já gravados,
 * já que o projeto não tem migração.</p>
 */
public enum TipoOcupacao {
    /** Imóvel próprio da família. */
    PROPRIEDADE("Propriedade"),
    /** Imóvel alugado. */
    LOCACAO("Locação"),
    /** Imóvel cedido por comodato. */
    COMODATO("Comodato"),
    /** Posse sem título formal. */
    POSSE("Posse"),
    /** Propriedade adquirida por usucapião. */
    USUCAPIAO("Usucapião");

    /** Descrição legível, usada apenas em interface. */
    private final String descricao;

    /**
     * Construtor do enum.
     *
     * @param descricao descrição legível
     */
    TipoOcupacao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Devolve a descrição legível da ocupação.
     *
     * @return texto exibível
     */
    public String getDescricao() {
        return descricao;
    }
}
