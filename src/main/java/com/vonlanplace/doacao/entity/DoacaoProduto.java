package com.vonlanplace.doacao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@IdClass(DoacaoProdutoId.class) // 1. Vincula a classe de chave composta
@Getter
@Setter
public class DoacaoProduto {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "doacao_id", nullable = false)
    private Doacao doacao;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "data_validade")
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