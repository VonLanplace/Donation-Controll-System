package com.vonlanplace.doacao.produto.tipo;

/**
 * Representação de saída de um {@link TipoProduto}.
 *
 * <p>Não expõe {@code id} de propósito: o tipo é identificado pelo nome em toda a API,
 * e é esta forma aninhada que aparece dentro de {@code ProdutoResponseDTO}, em
 * {@code GET /api/produto}.</p>
 *
 * @param nome nome do tipo
 */
public record TipoProdutoResponseDTO(
        String nome
) {
}
