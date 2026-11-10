package com.vonlanplace.doacao.recebedor.cesta;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Resultado da verificação de elegibilidade para receber a cesta.
 *
 * @param recebedorId          identificador do recebedor
 * @param nomeCompleto         nome do recebedor no cadastro atual
 * @param dataReferencia       primeiro dia do mês consultado
 * @param elegivel             se pode receber no mês consultado
 * @param mesesConsecutivos    meses anteriores consecutivos com {@code RECEBEU}
 * @param ultimoRecebimento    data do último {@code RECEBEU}, ou {@code null}
 * @param proximaDataPermitida primeiro dia do próximo mês permitido, ou {@code null}
 * @param motivo               explicação legível do resultado
 */
public record ElegibilidadeCestaDTO(
        UUID recebedorId,
        String nomeCompleto,
        LocalDate dataReferencia,
        boolean elegivel,
        int mesesConsecutivos,
        LocalDate ultimoRecebimento,
        LocalDate proximaDataPermitida,
        String motivo
) {
}
