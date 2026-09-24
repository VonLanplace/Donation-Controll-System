package com.vonlanplace.doacao.produto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProdutoUpdateDTO(
        String codigoBarras,
        @NotNull
        UUID marcaProdutoId,
        @NotNull
        UUID tipoProdutoId
) {
}
