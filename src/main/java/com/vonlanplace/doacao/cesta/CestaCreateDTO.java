package com.vonlanplace.doacao.cesta;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Dados de entrada para o registro de uma {@link Cesta}.
 *
 * <p><strong>Nenhum campo é obrigatório.</strong> Faltou {@code @NotNull} para ambos: uma
 * cesta pode ser criada já como entrega realizada, e pode ser criada antes de saber quem
 * vai receber. É decisão de modelagem, não esquecimento — a coluna da data não é
 * {@code nullable = false} e a FK do recebedor aceita {@code null}.</p>
 *
 * @param dataEntrega data da entrega, opcional
 * @param recebedorId recebedor que recebeu a cesta, opcional
 */
public record CestaCreateDTO(
        LocalDate dataEntrega,
        UUID recebedorId
) {
}
