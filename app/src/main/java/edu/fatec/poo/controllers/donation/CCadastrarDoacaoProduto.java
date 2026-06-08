package edu.fatec.poo.controllers.donation;

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
import edu.fatec.poo.util.TipoProdutoStringConverter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;

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

    public TipoProdutoService getTipoService() {
        return tipoService;
    }

    public void setTipoService(TipoProdutoService tipoService) {
        this.tipoService = tipoService;
    }

    public MarcaProdutoService getMarcaService() {
        return marcaService;
    }

    public void setMarcaService(MarcaProdutoService marcaService) {
        this.marcaService = marcaService;
    }

    public String getCodigo() {
        return codigo.get();
    }

    public void setCodigo(String codigo) {
        this.codigo.set(codigo);
    }

    public StringProperty codigoProperty() {
        return codigo;
    }

    public ObservableList<TipoProduto> getTiposCadastrados() {
        return tiposCadastrados;
    }

    public void setTiposCadastrados(ObservableList<TipoProduto> tiposCadastrados) {
        this.tiposCadastrados = tiposCadastrados;
    }

    public StringConverter<TipoProduto> getTiposConverter() {
        return tiposConverter.get();
    }

    public void setTiposConverter(StringConverter<TipoProduto> tiposConverter) {
        this.tiposConverter.set(tiposConverter);
    }

    public SimpleObjectProperty<StringConverter<TipoProduto>> tiposConverterProperty() {
        return tiposConverter;
    }

    public ObservableList<MarcaProduto> getMarcasCadastradas() {
        return marcasCadastradas;
    }

    public void setMarcasCadastradas(ObservableList<MarcaProduto> marcasCadastradas) {
        this.marcasCadastradas = marcasCadastradas;
    }

    public StringConverter<MarcaProduto> getMarcasConverter() {
        return marcasConverter.get();
    }

    public void setMarcasConverter(StringConverter<MarcaProduto> marcasConverter) {
        this.marcasConverter.set(marcasConverter);
    }

    public SimpleObjectProperty<StringConverter<MarcaProduto>> marcasConverterProperty() {
        return marcasConverter;
    }

    public TipoProduto getTipoSelecionado() {
        return tipoSelecionado.get();
    }

    public void setTipoSelecionado(TipoProduto tipoSelecionado) {
        this.tipoSelecionado.set(tipoSelecionado);
    }

    public ObjectProperty<TipoProduto> tipoSelecionadoProperty() {
        return tipoSelecionado;
    }

    public MarcaProduto getMarcaSelecionada() {
        return marcaSelecionada.get();
    }

    public void setMarcaSelecionada(MarcaProduto marcaSelecionada) {
        this.marcaSelecionada.set(marcaSelecionada);
    }

    public ObjectProperty<MarcaProduto> marcaSelecionadaProperty() {
        return marcaSelecionada;
    }

    public LocalDate getValidade() {
        return validade.get();
    }

    public void setValidade(LocalDate validade) {
        this.validade.set(validade);
    }

    public ObjectProperty<LocalDate> validadeProperty() {
        return validade;
    }

    public Produto getProdutoCriado() {
        return produtoCriado.get();
    }

    public void setProdutoCriado(Produto produtoCriado) {
        this.produtoCriado.set(produtoCriado);
    }

    public ObjectProperty<Produto> produtoCriadoProperty() {
        return produtoCriado;
    }
}
