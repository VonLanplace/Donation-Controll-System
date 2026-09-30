package com.vonlanplace.doacao.produto.tipo;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada para a criação de um {@link TipoProduto}.
 *
 * @param nome nome do tipo, obrigatório e único
 */
public record TipoProdutoCreateDTO(
        @NotBlank(message = "O nome do tipo de produto é obrigatório")
        String nome
) {
}
