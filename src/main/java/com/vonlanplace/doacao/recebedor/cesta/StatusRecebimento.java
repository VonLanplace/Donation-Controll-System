package com.vonlanplace.doacao.recebedor.cesta;

import lombok.Getter;

/**
 * Resultado da oferta de cesta em um mês.
 *
 * <p><strong>Recusa não conta para o limite de quatro meses consecutivos.</strong>
 * Apenas {@link #RECEBEU} entra na contagem; {@link #RECUSOU} funciona como
 * intervalo e reinicia a sequência.</p>
 */
@Getter
public enum StatusRecebimento {

    /** A pessoa recebeu a cesta no mês de referência. */
    RECEBEU("Recebeu"),

    /** A pessoa foi consultada, mas recusou a cesta no mês de referência. */
    RECUSOU("Recusou");

    /** Descrição legível usada nas telas e no CSV. */
    private final String descricao;

    /**
     * Construtor do enum.
     *
     * @param descricao descrição legível
     */
    StatusRecebimento(String descricao) {
        this.descricao = descricao;
    }
}
