package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.views.donation.UICadastarDoacaoProduto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CCadastrarDoacao {
    private StringProperty nomeDoador = new SimpleStringProperty();
    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    // TODO Use the DTO
    private ObjectProperty<Produto> produtoSelecionado = new SimpleObjectProperty<>();
    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    public void hello() {
        System.out.println("Hello");
    }

    public void start() {
        date.setValue(LocalDate.now());
    }

    public void adicionar() {
        //TODO
        System.out.println("ADICIONAR");
        try {
            UICadastarDoacaoProduto uiProduto = new UICadastarDoacaoProduto();
            Stage newStage = new Stage();
            uiProduto.start(newStage);
            Produto produtoNovo = uiProduto.getProdutoNovo();
            if (produtoNovo != null) {
                listaProdutos.add(produtoNovo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void remover() {
        //TODO
        System.out.println("REMOVER");
        // TODO Confirmar PopUp
        listaProdutos.remove(produtoSelecionado.get());
    }

    public void cadastrar() {
        //TODO
        System.out.println("CADASTRAR");
    }

    public String getNomeDoador() {
        return nomeDoador.get();
    }

    public StringProperty nomeDoadorProperty() {
        return nomeDoador;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public Produto getProdutoSelecionado() {
        return produtoSelecionado.get();
    }

    public ObjectProperty<Produto> produtoSelecionadoProperty() {
        return produtoSelecionado;
    }

}
