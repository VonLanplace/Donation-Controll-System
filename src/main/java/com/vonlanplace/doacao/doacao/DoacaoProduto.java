package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.produto.Produto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Item de uma {@link Doacao}: um {@link Produto} e a quantidade doada.
 *
 * <p><strong>É a entidade de junção de uma relação N:N com atributos.</strong> Não tem id
 * próprio — a identidade vem da chave composta {@link DoacaoProdutoId}, formada pelo par
 * doação e produto. Isso significa que o mesmo produto não pode aparecer duas vezes na
 * mesma doação; para duas quantidades diferentes, o cliente soma antes e manda um item
 * só.</p>
 *
 * <p><strong>Por que {@code equals} e {@code hashCode} são escritos à mão e o
 * {@code @ToString} é restrito.</strong> Esta entidade não usa {@code @Data}. A
 * igualdade compara os <em>ids</em> dos dois lados, extraídos por
 * {@link #doacaoId(Doacao)} e {@link #produtoId(Produto)}, e não as entidades: comparar
 * as entidades recursaria. O mesmo par de ids que identifica a linha também identifica
 * a instância dentro de um {@code Set}, que é o que permite ao Hibernate detectar
 * inserções e remoções ao substituir a lista inteira. Os campos privados
 * {@code doacaoId} e {@code produtoId} existem só por causa disso.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "doacao_produto")
@IdClass(DoacaoProdutoId.class)
public class DoacaoProduto {

    /** Lado da chave composta: a doação a que o item pertence. */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "doacao_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_doacao_produto_doacao")
    )
    private Doacao doacao;

    /** Lado da chave composta: o produto doado. */
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "produto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_doacao_produto_produto")
    )
    private Produto produto;

    /** Quantidade doada. Padrão 1. */
    @Column(name = "quantidade", nullable = false)
    @ToString.Include
    private int quantidade = 1;

    /** Validade do item. Obrigatória. */
    @Column(name = "data_validade", nullable = false)
    @ToString.Include
    private LocalDate dataValidade;

    /**
     * Compara dois itens pela chave composta.
     *
     * @param o objeto a comparar
     * @return {@code true} se apontarem para a mesma doação e o mesmo produto
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoacaoProduto that)) return false;
        return Objects.equals(doacaoId(doacao), doacaoId(that.doacao))
                && Objects.equals(produtoId(produto), produtoId(that.produto));
    }

    /**
     * Hash derivado da chave composta, para manter o contrato com {@link #equals(Object)}.
     *
     * @return hash do par de ids
     */
    @Override
    public int hashCode() {
        return Objects.hash(doacaoId(doacao), produtoId(produto));
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
