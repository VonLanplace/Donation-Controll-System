package com.vonlanplace.doacao.recebedor.form;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Linha de auxílio do formulário unificado de recebedor.
 *
 * <p>O {@code id} é {@code null} para uma linha nova e preenchido para uma linha
 * já persistida. A validação é feita pelo {@link RecebedorFormValidator}.</p>
 */
@Getter
@Setter
public class AuxilioFormDTO {

    /** Identificador do auxílio, ou {@code null} numa linha nova. */
    private UUID id;

    /** Valor mensal em reais. Obrigatório quando a linha está preenchida. */
    private BigDecimal valor;

    /** Origem do auxílio. Obrigatória quando a linha está preenchida. */
    private String fonte;

    /** Detalhe livre do auxílio. Opcional. */
    private String descricao;
}
