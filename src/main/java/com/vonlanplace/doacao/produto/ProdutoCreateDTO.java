package com.vonlanplace.doacao.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada para a criação de um {@link Produto}.
 *
 * <p>Marca e tipo são informados pelo {@code nome}, não pelo {@code id}: a chave de
 * negócio dessas entidades é o nome, e o serviço resolve a relação a partir dele.</p>
 *
 * @param codigoBarras código de barras do produto, opcional, no máximo 13 caracteres
 * @param marcaProduto nome da marca do produto, obrigatório
 * @param tipoProduto  nome do tipo do produto, obrigatório
 */
public record ProdutoCreateDTO(
        @Size(max = 13, message = "O código de barras deve ter no máximo 13 caracteres")
        String codigoBarras,
        @NotBlank(message = "O nome da marca do produto é obrigatório")
        String marcaProduto,
        @NotBlank(message = "O nome do tipo de produto é obrigatório")
        String tipoProduto
) {
}
