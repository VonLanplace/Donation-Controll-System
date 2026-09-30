package com.vonlanplace.doacao.doacao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Dados de entrada para o registro de uma {@link Doacao}.
 *
 * <p><strong>Todos os identificadores são referências opcionais, não chaves.</strong>
 * {@code recebedorId} e {@code entrevistadorId} podem vir {@code null}, o que produz uma
 * doação sem vínculo. Quando vêm preenchidos, o serviço valida a existência e devolve 404
 * se não acharem.</p>
 *
 * <p><strong>Produtos, recebedor e entrevistador não são obrigatórios pela Bean
 * Validation.</strong> A anotação {@link Valid} em {@code produtos} só propaga a validação
 * para os itens, quando houver itens. Uma lista vazia ou ausente é aceita e grava uma
 * doação sem itens.</p>
 *
 * @param data           data da doação, obrigatória
 * @param nomeDoador     nome de quem doou, opcional, no máximo 150 caracteres
 * @param recebedorId    recebedor contemplado, opcional
 * @param entrevistadorId entrevistador responsável, opcional
 * @param produtos       itens doados, opcional
 */
public record DoacaoCreateDTO(
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
