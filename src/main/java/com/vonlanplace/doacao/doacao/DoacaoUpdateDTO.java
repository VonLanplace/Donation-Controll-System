package com.vonlanplace.doacao.doacao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Dados de entrada para a edição de uma {@link Doacao}.
 *
 * <p><strong>A lista de produtos é substituída por inteiro, não mesclada.</strong> O
 * serviço apaga todos os itens atuais e insere os que vierem no corpo; um item que
 * desapareça da lista é removido do banco. Passar lista vazia deixa a doação sem itens, o
 * que é permitido.</p>
 *
 * <p>Tem o mesmo formato de {@link DoacaoCreateDTO}: os campos são substituídos, então
 * a data tem de vir de novo.</p>
 *
 * @param data           data da doação, obrigatória
 * @param nomeDoador     nome de quem doou, opcional, no máximo 150 caracteres
 * @param recebedorId    recebedor contemplado, opcional
 * @param entrevistadorId entrevistador responsável, opcional
 * @param produtos       lista completa de itens; substitui a anterior
 */
public record DoacaoUpdateDTO(
        @NotNull(message = "A data da doação é obrigatória")
        LocalDate data,
        @Size(max = 150, message = "O nome do doador deve ter no máximo 150 caracteres")
        String nomeDoador,
        UUID recebedorId,
        UUID entrevistadorId,
        @Valid
        List<DoacaoProdutoCreateDTO> produtos
) {
}
