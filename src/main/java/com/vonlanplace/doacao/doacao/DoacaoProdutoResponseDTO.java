package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.produto.ProdutoResponseDTO;

import java.time.LocalDate;

/**
 * Representação de saída de um item de {@link Doacao}.
 *
 * <p>Não repete o par de chaves: o id da doação está no DTO pai e o id do produto está
 * dentro de {@code produto}, que também traz marca e tipo. Para listar itens com o id do
 * produto isolado, é preciso extraí-lo do aninhamento.</p>
 *
 * @param produto      produto doado, com marca e tipo
 * @param quantidade   quantidade doada
 * @param dataValidade validade do item
 */
public record DoacaoProdutoResponseDTO(
        ProdutoResponseDTO produto,
        int quantidade,
        LocalDate dataValidade
) {
}
