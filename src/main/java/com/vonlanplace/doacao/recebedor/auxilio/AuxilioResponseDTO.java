package com.vonlanplace.doacao.recebedor.auxilio;

import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representação de saída de um {@link Auxilio}.
 *
 * <p>Aninha o recebedor inteiro, como as demais respostas filhas do
 * recebedor (cesta, doação).</p>
 *
 * @param id        identificador do auxílio
 * @param valor     valor mensal em reais
 * @param fonte     origem do auxílio
 * @param descricao detalhe livre, ou {@code null}
 * @param recebedor recebedor que recebe o auxílio
 */
public record AuxilioResponseDTO(
        UUID id,
        BigDecimal valor,
        String fonte,
        String descricao,
        RecebedorResponseDTO recebedor
) {
}