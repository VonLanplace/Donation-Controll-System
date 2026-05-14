package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.views.UICoordenador;
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
    private final UICoordenador coordenador;
    private final Usuario usuarioLogado;

    private StringProperty nomeDoador = new SimpleStringProperty();
    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    private ObjectProperty<Produto> produtoSelecionado = new SimpleObjectProperty<>();
    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    public CCadastrarDoacao(Usuario usuarioLogado, UICoordenador coordenador) {
        this.coordenador = coordenador;
        this.usuarioLogado = usuarioLogado;
    }

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
            coordenador.getScene().getRoot().setDisable(true);
            UICadastarDoacaoProduto uiProduto = new UICadastarDoacaoProduto();
            Stage newStage = new Stage();
            uiProduto.start(newStage);
            Produto produtoNovo = uiProduto.getProdutoNovo();
            if (produtoNovo != null) {
                listaProdutos.add(produtoNovo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            coordenador.getScene().getRoot().setDisable(false);
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

    public void cancelar() {
        System.out.println("CANCELAR");
        coordenador.returnToPreviosScreen();
    }
}
