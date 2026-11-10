package com.vonlanplace.doacao.recebedor.residencia.despesa;

import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Dados de entrada para o registro de uma {@link Despesa}.
 *
 * <p>A residência vem pelo {@code residenciaId}: a despesa é um sub-registro do
 * endereço, e o serviço resolve o vínculo antes de salvar.</p>
 *
 * @param valor        valor mensal em reais, obrigatório e positivo
 * @param fonte        origem da despesa, como "aluguel" ou "conta de luz"
 * @param descricao    detalhe livre, opcional
 * @param residenciaId identificador da residência a que a despesa pertence
 */
public record DespesaCreateDTO(
        @NotNull(message = "O valor da despesa é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor da despesa deve ser positivo")
        BigDecimal valor,
        @NotBlank(message = "A fonte da despesa é obrigatória")
        @Size(max = 100, message = "A fonte da despesa deve ter no máximo 100 caracteres")
        String fonte,
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String descricao,
        @NotNull(message = "A residência é obrigatória")
        UUID residenciaId
) {
}