package com.vonlanplace.doacao.produto.marca;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada para a atualização de uma {@link MarcaProduto}.
 *
 * <p>O nome atual vem do path da requisição e o novo nome do corpo; o serviço recusa a
 * alteração se o novo nome já pertencer a outra marca.</p>
 *
 * @param nome novo nome da marca, obrigatório e único
 */
public record MarcaProdutoUpdateDTO(
        @NotBlank(message = "O nome da marca é obrigatório")
        String nome
) {
}
