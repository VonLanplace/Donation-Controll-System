package com.vonlanplace.doacao.recebedor.cesta;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Dados de entrada para registrar uma oferta de cesta em um mês explícito.
 *
 * <p>A tela web usa o mês corrente e não passa por este DTO; ele existe para a API e
 * para lançamentos retroativos.</p>
 *
 * @param ano         ano de referência
 * @param mes         mês de referência
 * @param status      recebeu ou recusou
 * @param dataEntrega data efetiva da entrega, opcional
 * @param observacao  observação livre, opcional
 */
public record RecebimentoCestaCreateDTO(
        @NotNull(message = "O ano é obrigatório")
        @Min(value = 1900, message = "O ano deve ser maior ou igual a 1900")
        @Max(value = 2100, message = "O ano deve ser menor ou igual a 2100")
        Integer ano,
        @NotNull(message = "O mês é obrigatório")
        MesRecebimento mes,
        @NotNull(message = "O status é obrigatório")
        StatusRecebimento status,
        @PastOrPresent(message = "A data de entrega não pode ser futura")
        LocalDate dataEntrega,
        @Size(max = 1000, message = "A observação deve ter no máximo 1000 caracteres")
        String observacao
) {
}
