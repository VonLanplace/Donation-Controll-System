package com.vonlanplace.doacao.recebedor.dependente;

import com.vonlanplace.doacao.recebedor.Recebedor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Dados de entrada para a atualização de um {@link Dependente}.
 *
 * <p>Copia o create, menos a semântica de "novo": o id vem pela URL e o
 * dependente pode ser movido para outro recebedor trocando o {@code recebedorId}.</p>
 *
 * @param nome           nome do dependente, obrigatório
 * @param idade          idade informada na entrevista
 * @param dataNascimento data de nascimento, opcional, sempre no passado
 * @param recebedorId    identificador do recebedor a quem pertence
 */
public record DependenteUpdateDTO(
        @NotBlank(message = "O nome do dependente é obrigatório")
        @Size(max = 150, message = "O nome do dependente deve ter no máximo 150 caracteres")
        String nome,
        @PositiveOrZero(message = "A idade não pode ser negativa")
        int idade,
        @Past(message = "A data de nascimento deve ser no passado")
        LocalDate dataNascimento,
        @NotNull(message = "O recebedor é obrigatório")
        UUID recebedorId
) {
}