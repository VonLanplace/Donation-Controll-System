package com.vonlanplace.doacao.recebedor.form;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Linha de despesa do formulário unificado de recebedor.
 *
 * <p>O {@code id} é {@code null} para uma linha nova e preenchido para uma linha
 * já persistida. A despesa continua vinculada à residência do recebedor.</p>
 */
@Getter
@Setter
public class DespesaFormDTO {

    /** Identificador da despesa, ou {@code null} numa linha nova. */
    private UUID id;

    /** Valor mensal em reais. Obrigatório quando a linha está preenchida. */
    private BigDecimal valor;

    /** Origem da despesa. Obrigatória quando a linha está preenchida. */
    private String fonte;

    /** Detalhe livre da despesa. Opcional. */
    private String descricao;
}
