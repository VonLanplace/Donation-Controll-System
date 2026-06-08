package edu.fatec.poo.model.produto;

import edu.fatec.poo.model.IEntity;

import java.util.Objects;

public class TipoProduto implements IEntity {
    private Long id;
    private String nome;

    public TipoProduto() {
    }

    public TipoProduto(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "TipoProduto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TipoProduto that = (TipoProduto) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }
}
