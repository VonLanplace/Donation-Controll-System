package com.vonlanplace.doacao.produto.tipo;

import jakarta.validation.constraints.NotBlank;

public record TipoProdutoCreateDTO(
        @NotBlank(message = "O nome do tipo de produto é obrigatório")
        String nome
) {
}
