package com.vonlanplace.doacao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "doacao_produto")
@IdClass(DoacaoProdutoId.class)
@Getter
@Setter
public class DoacaoProduto {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "doacao_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_doacao_produto_doacao")
    )
    private Doacao doacao;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "produto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_doacao_produto_produto")
    )
    private Produto produto;

    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoacaoProduto that = (DoacaoProduto) o;
        return Objects.equals(doacao, that.doacao) &&
                Objects.equals(produto, that.produto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doacao, produto);
    }
}