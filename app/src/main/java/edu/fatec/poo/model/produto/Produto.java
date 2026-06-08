package edu.fatec.poo.model.produto;

import edu.fatec.poo.model.Cesta;
import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.IEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Produto implements IEntity {
    private Long id;
    private Doacao doacao;
    private MarcaProduto marca;
    private TipoProduto tipo;
    private Cesta cesta;
    private String codigoBarras;
    private LocalDate dataValidade;

    public Produto(Long id, Doacao doacao, MarcaProduto marca, TipoProduto tipo, Cesta cesta, String codigoBarras, LocalDate dataValidade) {
        this.id = id;
        this.doacao = doacao;
        this.marca = marca;
        this.tipo = tipo;
        this.cesta = cesta;
        this.codigoBarras = codigoBarras;
        this.dataValidade = dataValidade;
    }

    public Produto() {
    }


    public boolean isInCesta() {
        return cesta != null;
    }

    public String getDataValidadeDdMmYyyy() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        return dataValidade.format(dtf);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Doacao getDoacao() {
        return doacao;
    }

    public void setDoacao(Doacao doacao) {
        this.doacao = doacao;
    }

    public MarcaProduto getMarca() {
        return marca;
    }

    public void setMarca(MarcaProduto marca) {
        this.marca = marca;
    }

    public TipoProduto getTipo() {
        return tipo;
    }

    public void setTipo(TipoProduto tipo) {
        this.tipo = tipo;
    }

    public Cesta getCesta() {
        return cesta;
    }

    public void setCesta(Cesta cesta) {
        this.cesta = cesta;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", doacao=" + doacao +
                ", marca=" + marca +
                ", tipo=" + tipo +
                ", cesta=" + cesta +
                ", codigoBarras='" + codigoBarras + '\'' +
                ", dataValidade=" + dataValidade +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id) && Objects.equals(doacao, produto.doacao) && Objects.equals(marca, produto.marca) && Objects.equals(tipo, produto.tipo) && Objects.equals(cesta, produto.cesta) && Objects.equals(codigoBarras, produto.codigoBarras) && Objects.equals(dataValidade, produto.dataValidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, doacao, marca, tipo, cesta, codigoBarras, dataValidade);
    }
}
