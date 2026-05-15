package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.model.produto.Doacao;
import edu.fatec.poo.model.produto.MarcaProduto;
import edu.fatec.poo.model.produto.TipoProduto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.time.LocalDate;

@Getter
@Setter
public class CCadastrarDoacaoProduto {

    private StringProperty codigo = new SimpleStringProperty();

    private ObservableList<TipoProduto> produtosCadastrados = FXCollections.observableArrayList();
    private ObservableList<MarcaProduto> marcasCadastradas = FXCollections.observableArrayList();

    private ObjectProperty<TipoProduto> tipoSelecionado = new SimpleObjectProperty<>();
    private ObjectProperty<MarcaProduto> marcaSelecionada = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> validade = new SimpleObjectProperty<>();

    private ObjectProperty<Doacao> produtoCriado = new SimpleObjectProperty<>();

    public CCadastrarDoacaoProduto() {

        validade.setValue(LocalDate.now());
    }

    public Doacao cadastrar() {
        return toEntity();
    }

    private Doacao toEntity() {
        try {
            Doacao doacao = new Doacao();
            doacao.setIdMarcaProduto(marcaSelecionada.get() == null ? 0 : marcaSelecionada.get().getId());
            doacao.setIdTipoProduto(tipoSelecionado.get() == null ? 0 : tipoSelecionado.get().getId());
            doacao.setIdCesta(null);
            doacao.setCodigoBarras(codigo.get());
            doacao.setDataValidade(validade.get());
            return doacao;
        } catch (Exception e) {
            e.printStackTrace();
            showError(e);
        }
        return null;
    }

    public void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        e.printStackTrace();

        if (e instanceof SQLException sqlEx) {
            alert.setTitle("Erro de Banco de Dados");
            if (sqlEx.getErrorCode() == 1062) {
                alert.setContentText("Erro: Este CPF ou E-mail já está cadastrado.");
            } else {
                alert.setContentText("Falha na conexão: " + sqlEx.getMessage());
            }
        } else if (e instanceof IllegalArgumentException) {
            alert.setTitle("Dados Inválidos");
            alert.setContentText(e.getMessage());
        } else {
            alert.setTitle("Erro");
            alert.setContentText("Um erro inesperado ocorreu: " + e.getMessage());
        }
        alert.showAndWait();
    }
}
