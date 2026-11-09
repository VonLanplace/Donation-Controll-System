package com.vonlanplace.doacao.recebedor.cesta;

import lombok.Getter;

/**
 * Meses do ano usados na referência mensal do recebimento de cesta.
 *
 * <p><strong>O número é a chave canônica.</strong> A coluna {@code data_referencia} é
 * um {@code LocalDate} no primeiro dia do mês; o enum serve para entrada e exibição.
 * Persistir o enum como texto faria a ordenação do banco seguir a ordem alfabética
 * ("ABRIL" antes de "JANEIRO"), por isso a data é a fonte de verdade.</p>
 */
@Getter
public enum MesRecebimento {

    /** Janeiro. */
    JANEIRO(1, "Janeiro"),
    /** Fevereiro. */
    FEVEREIRO(2, "Fevereiro"),
    /** Março. */
    MARCO(3, "Março"),
    /** Abril. */
    ABRIL(4, "Abril"),
    /** Maio. */
    MAIO(5, "Maio"),
    /** Junho. */
    JUNHO(6, "Junho"),
    /** Julho. */
    JULHO(7, "Julho"),
    /** Agosto. */
    AGOSTO(8, "Agosto"),
    /** Setembro. */
    SETEMBRO(9, "Setembro"),
    /** Outubro. */
    OUTUBRO(10, "Outubro"),
    /** Novembro. */
    NOVEMBRO(11, "Novembro"),
    /** Dezembro. */
    DEZEMBRO(12, "Dezembro");

    /** Número do mês, de 1 a 12. */
    private final int numero;

    /** Descrição legível usada nas telas e no CSV. */
    private final String descricao;

    /**
     * Construtor do enum.
     *
     * @param numero    número do mês
     * @param descricao descrição legível
     */
    MesRecebimento(int numero, String descricao) {
        this.numero = numero;
        this.descricao = descricao;
    }

    /**
     * Resolve o mês pelo número.
     *
     * @param numero número de 1 a 12
     * @return o mês correspondente
     * @throws IllegalArgumentException se o número estiver fora do intervalo
     */
    public static MesRecebimento de(int numero) {
        for (MesRecebimento mes : values()) {
            if (mes.numero == numero) {
                return mes;
            }
        }
        throw new IllegalArgumentException("Mês inválido: " + numero);
    }
}
