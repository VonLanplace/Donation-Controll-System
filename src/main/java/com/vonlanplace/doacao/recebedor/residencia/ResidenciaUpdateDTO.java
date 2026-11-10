package com.vonlanplace.doacao.recebedor.residencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada para a edição de uma {@link Residencia}.
 *
 * <p>Mesmo formato de {@link ResidenciaCreateDTO}: a atualização substitui todos os
 * campos, então os obrigatórios precisam vir de novo no corpo da requisição.</p>
 *
 * @param logradouro     logradouro, obrigatório, no máximo 150 caracteres
 * @param numero         número, obrigatório, no máximo 20 caracteres
 * @param cep            CEP sem hífen, obrigatório, no máximo 8 caracteres
 * @param complemento    complemento, opcional
 * @param cidade         cidade, obrigatória, no máximo 100 caracteres
 * @param bairro         bairro, obrigatório, no máximo 100 caracteres
 * @param numMoradores   quantidade de moradores, não negativa
 * @param numTrabalhadores quantidade de trabalhadores, não negativa
 * @param tipoOcupacao   perfil de ocupação, obrigatório
 * @param tipoResidencia tipo de moradia, obrigatório
 */
public record ResidenciaUpdateDTO(
        @NotBlank(message = "O logradouro é obrigatório")
        @Size(max = 150, message = "O logradouro deve ter no máximo 150 caracteres")
        String logradouro,
        @NotBlank(message = "O número é obrigatório")
        @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
        String numero,
        @NotBlank(message = "O CEP é obrigatório")
        @Size(max = 8, message = "O CEP deve ter no máximo 8 caracteres")
        String cep,
        @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
        String complemento,
        @NotBlank(message = "A cidade é obrigatória")
        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
        String cidade,
        @NotBlank(message = "O bairro é obrigatório")
        @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
        String bairro,
        @PositiveOrZero(message = "O número de moradores não pode ser negativo")
        int numMoradores,
        @PositiveOrZero(message = "O número de trabalhadores não pode ser negativo")
        int numTrabalhadores,
        @NotNull(message = "O tipo de ocupação é obrigatório")
        TipoOcupacao tipoOcupacao,
        @NotNull(message = "O tipo de residência é obrigatório")
        TipoResidencia tipoResidencia
) {
}
