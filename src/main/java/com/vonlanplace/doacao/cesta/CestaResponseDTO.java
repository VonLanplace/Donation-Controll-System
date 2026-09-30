package com.vonlanplace.doacao.cesta;

import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Representação de saída de uma {@link Cesta}.
 *
 * <p>Aninha o recebedor inteiro, não só o id. Quando a cesta não tem vínculo, o campo
 * vem {@code null} no JSON.</p>
 *
 * @param id          identificador da cesta
 * @param dataEntrega data da entrega
 * @param recebedor   recebedor que recebeu, ou {@code null}
 */
public record CestaResponseDTO(
        UUID id,
        LocalDate dataEntrega,
        RecebedorResponseDTO recebedor
) {
}
