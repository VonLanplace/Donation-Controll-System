package com.vonlanplace.doacao.recebedor.residencia.despesa;

import com.vonlanplace.doacao.recebedor.residencia.ResidenciaResponseDTO;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representação de saída de uma {@link Despesa}.
 *
 * <p>Aninha a residência inteira, para que o entrevistador tenha o endereço
 * junto do valor sem uma segunda chamada.</p>
 *
 * @param id         identificador da despesa
 * @param valor      valor mensal em reais
 * @param fonte      origem da despesa
 * @param descricao  detalhe livre, ou {@code null}
 * @param residencia residência a que a despesa pertence
 */
public record DespesaResponseDTO(
        UUID id,
        BigDecimal valor,
        String fonte,
        String descricao,
        ResidenciaResponseDTO residencia
) {
}