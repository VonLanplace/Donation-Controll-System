package com.vonlanplace.doacao.produto.tipo;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada para a atualização de um {@link TipoProduto}.
 *
 * <p>O nome atual vem do path da requisição e o novo nome do corpo; o serviço recusa a
 * alteração se o novo nome já pertencer a outro tipo.</p>
 *
 * @param nome novo nome do tipo, obrigatório e único
 */
public record TipoProdutoUpdateDTO(
        @NotBlank(message = "O nome do tipo de produto não pode ser vazio")
        String nome
) {
}
