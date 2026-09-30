package com.vonlanplace.doacao.doacao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Dados de entrada de um item de {@link Doacao}.
 *
 * <p>O mesmo record serve para criação e para atualização: na atualização cada item do
 * corpo descreve a linha final, não um patch.</p>
 *
 * <p><strong>Produto repetido é erro, não soma.</strong> A chave composta de
 * {@code DoacaoProduto} impede o mesmo produto duas vezes na mesma doação, então o
 * serviço valida a lista antes de gravar e devolve 409 em vez de deixar o banco recusar
 * com violação de constraint. Para quantidades maiores, mande um item só com a
 * quantidade somada.</p>
 *
 * @param produtoId    produto doado, obrigatório
 * @param quantidade   quantidade doada, maior que zero
 * @param dataValidade validade do item, obrigatória
 */
public record DoacaoProdutoCreateDTO(
        @NotNull(message = "O produto é obrigatório")
        UUID produtoId,
        @Positive(message = "A quantidade deve ser maior que zero")
        int quantidade,
        @NotNull(message = "A data de validade é obrigatória")
        LocalDate dataValidade
) {
}
