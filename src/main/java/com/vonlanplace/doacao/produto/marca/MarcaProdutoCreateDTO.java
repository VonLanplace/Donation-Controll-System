package com.vonlanplace.doacao.produto.marca;

import jakarta.validation.constraints.NotBlank;

public record MarcaProdutoCreateDTO(
        @NotBlank(message = "O nome da marca é obrigatório")
        String nome
) {
}
