package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProdutoResponseDTO;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoResponseDTO;

import java.util.UUID;

/**
 * Representação de saída de um {@link Produto}.
 *
 * <p>Marca e tipo são aninhados como objetos e <strong>sem {@code id}</strong>: a resposta
 * expõe apenas o nome de cada um, conforme o contrato de {@code GET /api/produto}.</p>
 *
 * <p>O campo {@code cesta} da entidade não aparece aqui porque não existe caminho de
 * escrita para ele.</p>
 *
 * @param id           identificador do produto
 * @param codigoBarras código de barras do produto
 * @param marcaProduto marca do produto, somente nome
 * @param tipoProduto  tipo do produto, somente nome
 */
public record ProdutoResponseDTO(
        UUID id,
        String codigoBarras,
        MarcaProdutoResponseDTO marcaProduto,
        TipoProdutoResponseDTO tipoProduto
) {
}
