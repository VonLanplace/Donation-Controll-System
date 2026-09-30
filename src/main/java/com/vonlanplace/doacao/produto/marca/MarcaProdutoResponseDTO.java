package com.vonlanplace.doacao.produto.marca;

/**
 * Representação de saída de uma {@link MarcaProduto}.
 *
 * <p>Não expõe {@code id} de propósito: a marca é identificada pelo nome em toda a API,
 * e é esta forma aninhada que aparece dentro de {@code ProdutoResponseDTO}, em
 * {@code GET /api/produto}.</p>
 *
 * @param nome nome da marca
 */
public record MarcaProdutoResponseDTO(
        String nome
) {
}
