package com.vonlanplace.doacao.produto.tipo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TipoProdutoUpdateDTO(
        @NotNull(message = "O ID é obrigatório para atualização")
        UUID id,

        @NotBlank(message = "O nome do tipo de produto não pode ser vazio")
        String nome
) {
}
