package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.model.produto.MarcaProduto;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.model.produto.TipoProduto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CCadastrarDoacaoProduto {

    private StringProperty codigo = new SimpleStringProperty();
    private ObjectProperty<TipoProduto> tipo = new SimpleObjectProperty<>();
    private ObjectProperty<MarcaProduto> marca = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> validade = new SimpleObjectProperty<>();

    private ObjectProperty<Produto> produtoCriado = new SimpleObjectProperty<>();

    public void start() {
        validade.setValue(LocalDate.now());
    }

    public Produto cadastrar() {
        // TODO
        System.out.println("CADASTRAR");
        return new Produto((long) ((Math.random() * (100)) + 0), 10L, 10L, 10L, UUID.randomUUID().toString(), LocalDate.now());
    }

    public String getCodigo() {
        return codigo.get();
    }

    public StringProperty codigoProperty() {
        return codigo;
    }

    public TipoProduto getTipo() {
        return tipo.get();
    }

    public ObjectProperty<TipoProduto> tipoProperty() {
        return tipo;
    }

    public MarcaProduto getMarca() {
        return marca.get();
    }

    public ObjectProperty<MarcaProduto> marcaProperty() {
        return marca;
    }

    public LocalDate getValidade() {
        return validade.get();
    }

    public ObjectProperty<LocalDate> validadeProperty() {
        return validade;
    }

}
