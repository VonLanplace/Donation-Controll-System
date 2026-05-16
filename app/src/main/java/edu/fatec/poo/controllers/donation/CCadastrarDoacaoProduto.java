package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.TipoProdutoStringConverter;
import edu.fatec.poo.adapter.produto.DtoProdutoCadastrarDoacao;
import edu.fatec.poo.model.produto.MarcaProduto;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.model.produto.TipoProduto;
import edu.fatec.poo.persistence.connection.CurrentConnection;
import edu.fatec.poo.persistence.entityDao.MarcaProdutoDao;
import edu.fatec.poo.persistence.entityDao.TipoProdutoDao;
import edu.fatec.poo.service.MarcaProdutoService;
import edu.fatec.poo.service.TipoProdutoService;
import edu.fatec.poo.util.MarcaProdutoStringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.time.LocalDate;

@Getter
@Setter
public class CCadastrarDoacaoProduto {

    private TipoProdutoService tipoService;
    private MarcaProdutoService marcaService;

    private StringProperty codigo = new SimpleStringProperty();

    private ObservableList<TipoProduto> tiposCadastrados = FXCollections.observableArrayList();
    private SimpleObjectProperty<StringConverter<TipoProduto>> tiposConverter = new SimpleObjectProperty<>(new TipoProdutoStringConverter());

    private ObservableList<MarcaProduto> marcasCadastradas = FXCollections.observableArrayList();
    private SimpleObjectProperty<StringConverter<MarcaProduto>> marcasConverter = new SimpleObjectProperty<>(new MarcaProdutoStringConverter());

    private ObjectProperty<TipoProduto> tipoSelecionado = new SimpleObjectProperty<>();
    private ObjectProperty<MarcaProduto> marcaSelecionada = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> validade = new SimpleObjectProperty<>();

    private ObjectProperty<Produto> produtoCriado = new SimpleObjectProperty<>();

    public CCadastrarDoacaoProduto() {
        validade.setValue(LocalDate.now());
        try {
            CurrentConnection connector = new CurrentConnection();

            TipoProdutoDao tipoDao = new TipoProdutoDao(connector.getConector());
            tipoService = new TipoProdutoService(tipoDao);
            tiposCadastrados.setAll(tipoService.searchAll());

            MarcaProdutoDao marcaDao = new MarcaProdutoDao(connector.getConector());
            marcaService = new MarcaProdutoService(marcaDao);
            marcasCadastradas.setAll(marcaService.searchAll());
        } catch (Exception e) {
            showError(e);
        }
    }

    public Produto cadastrar() {
        return toEntity();
    }

    private Produto toEntity() {
        try {
            DtoProdutoCadastrarDoacao dto = new DtoProdutoCadastrarDoacao(
                    marcaSelecionada.get(),
                    tipoSelecionado.get(),
                    codigo.get(),
                    validade.get()
            );
            return dto.toProduto();
        } catch (Exception e) {
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
