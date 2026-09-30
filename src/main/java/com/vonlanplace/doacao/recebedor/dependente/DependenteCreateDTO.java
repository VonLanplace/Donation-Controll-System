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
 * Dados de entrada para o cadastro de um {@link Dependente}.
 *
 * <p>O recebedor vem pelo {@code recebedorId}: o dependente é um sub-registro
 * da pessoa, e o serviço resolve o vínculo antes de salvar. A idade é o dado
 * de rotina; a data de nascimento é opcional e, quando presente, precisa estar
 * no passado.</p>
 *
 * @param nome           nome do dependente, obrigatório
 * @param idade          idade informada na entrevista
 * @param dataNascimento data de nascimento, opcional, sempre no passado
 * @param recebedorId    identificador do recebedor a quem pertence
 */
public record DependenteCreateDTO(
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