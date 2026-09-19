package com.vonlanplace.doacao.produto.marca;

import java.util.UUID;

public record MarcaProdutoResponseDTO(
        UUID id,
        String nome
) {
}
