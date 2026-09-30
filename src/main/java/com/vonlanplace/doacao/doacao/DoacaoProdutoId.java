package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.produto.Produto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Chave composta de {@link DoacaoProduto}, formada pelo par doação e produto.
 *
 * <p><strong>Os campos desta classe não podem ter outro nome.</strong> O
 * {@code @IdClass} do JPA resolve a chave por nome: o campo {@code doacao} precisa
 * casar com o atributo {@code doacao} de {@link DoacaoProduto}, e {@code produto} com
 * {@code produto}. Não valem os nomes das colunas ({@code doacao_id},
 * {@code produto_id}) nem abreviações como {@code idDoacao}. Um nome errado não dá erro
 * de compilação — a combinação de nomes simplesmente nunca casa, e o efeito aparece
 * como chave sempre nula em tempo de execução.</p>
 *
 * <p><strong>Os tipos são as entidades, não os ids.</strong> A chave composta carrega as
 * referências {@link Doacao} e {@link Produto}, e a igualdade é calculada sobre os
 * {@code getId()} de cada lado, extraídos por
 * {@link #doacaoId(Doacao)} e {@link #produtoId(Produto)}. Comparar as entidades
 * diretamente recursaria, porque as duas têm coleções que apontam de volta.</p>
 *
 * <p>Implementa {@link Serializable} por exigência do JPA, com
 * {@code serialVersionUID} fixo.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoacaoProdutoId implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Lado da chave: a doação. */
    private Doacao doacao;

    /** Lado da chave: o produto. */
    private Produto produto;

    /**
     * Compara duas chaves pelo par de ids que cada uma representa.
     *
     * @param o objeto a comparar
     * @return {@code true} se apontarem para a mesma doação e o mesmo produto
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoacaoProdutoId that)) return false;
        return Objects.equals(doacaoId(doacao), doacaoId(that.doacao))
                && Objects.equals(produtoId(produto), produtoId(that.produto));
    }

    /**
     * Hash derivado do par de ids, para manter o contrato com {@link #equals(Object)}.
     *
     * @return hash do par de ids
     */
    @Override
    public int hashCode() {
        return Objects.hash(doacaoId(doacao), produtoId(produto));
    }

    /**
     * Representação legível com os ids, sem percorrer as entidades.
     *
     * @return texto com o par de ids
     */
    @Override
    public String toString() {
        return "DoacaoProdutoId[doacao=" + doacaoId(doacao) + ", produto=" + produtoId(produto) + "]";
    }

    /**
     * Extrai o id da doação sem percorrer a entidade.
     *
     * @param doacao doação, pode ser {@code null}
     * @return id da doação, ou {@code null}
     */
    private static UUID doacaoId(Doacao doacao) {
        return doacao == null ? null : doacao.getId();
    }

    /**
     * Extrai o id do produto sem percorrer a entidade.
     *
     * @param produto produto, pode ser {@code null}
     * @return id do produto, ou {@code null}
     */
    private static UUID produtoId(Produto produto) {
        return produto == null ? null : produto.getId();
    }
}
