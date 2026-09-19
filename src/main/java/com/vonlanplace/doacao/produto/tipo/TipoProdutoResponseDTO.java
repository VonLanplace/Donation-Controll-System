package com.vonlanplace.doacao.produto.tipo;

import java.util.UUID;

public record TipoProdutoResponseDTO(
        UUID id,
        String nome
) {
}
