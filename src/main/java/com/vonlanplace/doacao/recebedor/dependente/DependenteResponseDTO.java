package com.vonlanplace.doacao.recebedor.dependente;

import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Representação de saída de um {@link Dependente}.
 *
 * <p>Aninha o recebedor inteiro, para que a página do dependente não precise de
 * outra consulta.</p>
 *
 * @param id             identificador do dependente
 * @param nome           nome do dependente
 * @param idade          idade informada na entrevista
 * @param dataNascimento data de nascimento, ou {@code null} se desconhecida
 * @param recebedor      recebedor a quem o dependente pertence
 */
public record DependenteResponseDTO(
        UUID id,
        String nome,
        int idade,
        LocalDate dataNascimento,
        RecebedorResponseDTO recebedor
) {
}