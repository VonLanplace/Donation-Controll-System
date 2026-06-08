package edu.fatec.poo.model;

import edu.fatec.poo.model.produto.Produto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Doacao {
    private UUID id = UUID.randomUUID();
    private String nomeDoador;
    private LocalDate data;
    private Usuario cadastrante;
    private List<Produto> produtos;

    public Doacao() {
    }

    public Doacao(UUID id, String nomeDoador, LocalDate data, Usuario cadastrante, List<Produto> produtos) {
        this.id = id;
        this.nomeDoador = nomeDoador;
        this.data = data;
        this.cadastrante = cadastrante;
        this.produtos = produtos;
    }


    public String getDataValidadeDdMmYyyy() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        return data.format(dtf);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeDoador() {
        return nomeDoador;
    }

    public void setNomeDoador(String nomeDoador) {
        this.nomeDoador = nomeDoador;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Usuario getCadastrante() {
        return cadastrante;
    }

    public void setCadastrante(Usuario cadastrante) {
        this.cadastrante = cadastrante;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    @Override
    public String toString() {
        return "Doacao{" +
                "id=" + id +
                ", nomeDoador='" + nomeDoador + '\'' +
                ", data=" + data +
                ", cadastrante=" + cadastrante +
                ", produtos=" + produtos +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Doacao doacao = (Doacao) o;
        return Objects.equals(id, doacao.id) && Objects.equals(nomeDoador, doacao.nomeDoador) && Objects.equals(data, doacao.data) && Objects.equals(cadastrante, doacao.cadastrante) && Objects.equals(produtos, doacao.produtos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomeDoador, data, cadastrante, produtos);
    }
}
