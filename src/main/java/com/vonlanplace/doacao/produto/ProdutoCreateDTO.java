package com.vonlanplace.doacao.produto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProdutoCreateDTO(
        String codigoBarras,
        @NotNull(message = "O ID da marca do produto é obrigatório")
        UUID marcaProdutoId,
        @NotNull(message = "O ID do tipo de produto é obrigatório")
        UUID tipoProdutoId
) {
}
