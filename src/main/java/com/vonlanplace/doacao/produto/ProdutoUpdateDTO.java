package com.vonlanplace.doacao.produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de entrada para a atualização de um {@link Produto} existente.
 *
 * <p>Tem o mesmo formato de {@link ProdutoCreateDTO}. Os campos são aplicados sobre a
 * entidade já carregada; a marca e o tipo são resolvidos pelo {@code nome} antes da
 * aplicação, de modo que a atualização falha com
 * {@link jakarta.persistence.EntityNotFoundException} se qualquer um deles não existir.</p>
 *
 * @param codigoBarras novo código de barras do produto, opcional, no máximo 13 caracteres
 * @param marcaProduto nome da marca do produto, obrigatório
 * @param tipoProduto  nome do tipo do produto, obrigatório
 */
public record ProdutoUpdateDTO(
        @Size(max = 13, message = "O código de barras deve ter no máximo 13 caracteres")
        String codigoBarras,
        @NotBlank(message = "O nome da marca do produto é obrigatório")
        String marcaProduto,
        @NotBlank(message = "O nome do tipo de produto é obrigatório")
        String tipoProduto
) {
}
