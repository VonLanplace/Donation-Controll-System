package com.vonlanplace.doacao.cesta;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Dados de entrada para a edição de uma {@link Cesta}.
 *
 * <p>Mesmo formato de {@link CestaCreateDTO}, com os dois campos opcionais. O
 * {@code nullValuePropertyMappingStrategy.IGNORE} do {@code CestaMapper} não evita a
 * troca de recebedor: esse vínculo é resolvido à mão pelo {@code CestaService}, que
 * aceita {@code null} e desliga a cesta. Já a data de entrega, por vir do mapper com a
 * estratégia de ignorar {@code null}, continua como estava quando o campo é omitido no
 * corpo.</p>
 *
 * @param dataEntrega data da entrega, opcional
 * @param recebedorId recebedor que recebeu a cesta, opcional
 */
public record CestaUpdateDTO(
        LocalDate dataEntrega,
        UUID recebedorId
) {
}
