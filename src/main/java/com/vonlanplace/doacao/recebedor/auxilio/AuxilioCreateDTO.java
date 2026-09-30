package com.vonlanplace.doacao.recebedor.auxilio;

import com.vonlanplace.doacao.recebedor.Recebedor;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Dados de entrada para o registro de um {@link Auxilio}.
 *
 * <p>O recebedor vem pelo {@code recebedorId}: o auxílio é um sub-registro da
 * pessoa, e o serviço resolve o vínculo antes de salvar.</p>
 *
 * @param valor       valor mensal em reais, obrigatório e positivo
 * @param fonte       origem do auxílio, como "Bolsa Família" ou "ONG"
 * @param descricao   detalhe livre, opcional
 * @param recebedorId identificador do recebedor que recebe o auxílio
 */
public record AuxilioCreateDTO(
        @NotNull(message = "O valor do auxílio é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor do auxílio deve ser positivo")
        BigDecimal valor,
        @NotBlank(message = "A fonte do auxílio é obrigatória")
        @Size(max = 100, message = "A fonte do auxílio deve ter no máximo 100 caracteres")
        String fonte,
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String descricao,
        @NotNull(message = "O recebedor é obrigatório")
        UUID recebedorId
) {
}