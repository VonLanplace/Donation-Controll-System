package com.vonlanplace.doacao.recebedor.cesta;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Representação de saída de um {@link RecebimentoCesta}.
 *
 * @param id             identificador do registro
 * @param recebedorId    identificador do recebedor
 * @param dataReferencia primeiro dia do mês de referência
 * @param dataEntrega    data efetiva da entrega, ou {@code null}
 * @param status         recebeu ou recusou
 * @param snapshot       dados do recebedor no momento do registro
 * @param observacao     observação livre
 */
public record RecebimentoCestaResponseDTO(
        UUID id,
        UUID recebedorId,
        LocalDate dataReferencia,
        LocalDate dataEntrega,
        StatusRecebimento status,
        SnapshotRecebedor snapshot,
        String observacao
) {
}
