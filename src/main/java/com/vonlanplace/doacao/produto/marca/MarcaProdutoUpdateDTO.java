package com.vonlanplace.doacao.produto.marca;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarcaProdutoUpdateDTO(
        @NotNull
        UUID id,
        @NotBlank
        String nome
) {
}
