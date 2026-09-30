package com.vonlanplace.doacao.produto.marca;

import jakarta.validation.constraints.NotBlank;

/**
 * Dados de entrada para a criação de uma {@link MarcaProduto}.
 *
 * @param nome nome da marca, obrigatório e único
 */
public record MarcaProdutoCreateDTO(
        @NotBlank(message = "O nome da marca é obrigatório")
        String nome
) {
}
