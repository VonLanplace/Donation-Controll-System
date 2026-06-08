package edu.fatec.poo.views.donation;

import atlantafx.base.theme.Styles;
import edu.fatec.poo.controllers.donation.CCadastrarDoacaoProduto;
import edu.fatec.poo.model.produto.MarcaProduto;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.model.produto.TipoProduto;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class UICadastarDoacaoProduto extends Application {

    // Size Variables
    private static final double WHIDTH = 360;
    private static final double HEIGHT = 360;
    private static final double SPACING = 10;
    // TODO Use DTO
    Produto produtoNovo;
    private CCadastrarDoacaoProduto controller;
    private VBox paneMain;
    private VBox paneProduto;
    private Scene scene;
    private HBox paneButtonsBottom;
    private Label lblProduto;
    private Label lblCodigo;
    private Label lblTipo;
    private Label lblMarca;
    private Label lblValidade;
    private TextField txtCodigo;
    private ComboBox<TipoProduto> cbbTipo;
    private ComboBox<MarcaProduto> cbbMarca;
    private DatePicker dpcValidade;
    private Button btnCancelar;
    private Button btnCadastrar;

    @Override
    public void start(Stage stage) {
        controller = new CCadastrarDoacaoProduto();

        // Painel principal com espaçamento e preenchimento adequados
        paneMain = new VBox(20);
        paneMain.setPadding(new Insets(20));
        paneMain.setAlignment(Pos.TOP_LEFT);

        scene = new Scene(paneMain, 400, 450);

        lblProduto = new Label("Adicionar Produto");
        lblProduto.getStyleClass().add(Styles.TITLE_4);

        paneProduto = new VBox(5);
        paneProduto.setPadding(new Insets(5, 0, 5, 0));

        VBox boxCodigo = new VBox(5, new Label("Código de Barras"), txtCodigo = new TextField());
        txtCodigo.setPromptText("Opcional");
        txtCodigo.textProperty().bindBidirectional(controller.codigoProperty());

        VBox boxTipo = new VBox(5, new Label("Tipo"), cbbTipo = new ComboBox<>());
        cbbTipo.setMaxWidth(Double.MAX_VALUE);
        cbbTipo.setItems(controller.getTiposCadastrados());
        cbbTipo.valueProperty().bindBidirectional(controller.tipoSelecionadoProperty());
        cbbTipo.converterProperty().bindBidirectional(controller.tiposConverterProperty());

        VBox boxMarca = new VBox(5, new Label("Marca"), cbbMarca = new ComboBox<>());
        cbbMarca.setMaxWidth(Double.MAX_VALUE);
        cbbMarca.setItems(controller.getMarcasCadastradas());
        cbbMarca.valueProperty().bindBidirectional(controller.marcaSelecionadaProperty());
        cbbMarca.converterProperty().bindBidirectional(controller.marcasConverterProperty());

        VBox boxValidade = new VBox(5, new Label("Validade"), dpcValidade = new DatePicker(LocalDate.now()));
        dpcValidade.setMaxWidth(Double.MAX_VALUE);
        dpcValidade.valueProperty().bindBidirectional(controller.validadeProperty());

        paneProduto.getChildren().addAll(boxCodigo, boxTipo, boxMarca, boxValidade);

        paneButtonsBottom = new HBox(15);
        paneButtonsBottom.setAlignment(Pos.CENTER_RIGHT);

        btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add(Styles.FLAT);
        btnCancelar.setOnAction(p -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente sair?", ButtonType.YES, ButtonType.NO);
            alert.initOwner(stage);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) stage.close();
            });
        });

        btnCadastrar = new Button("Confirmar");
        btnCadastrar.getStyleClass().add(Styles.ACCENT);
        btnCadastrar.setPrefWidth(100);
        btnCadastrar.setDefaultButton(true);
        btnCadastrar.setOnAction(p -> {
            produtoNovo = controller.cadastrar();
            if (produtoNovo != null) {
                stage.close();
            }
        });

        paneButtonsBottom.getChildren().addAll(btnCancelar, btnCadastrar);

        paneMain.getChildren().addAll(lblProduto, new Separator(), paneProduto, paneButtonsBottom);

        stage.setTitle("Cadastro de Doação");
        stage.setResizable(false);
        stage.setScene(scene);

        stage.showAndWait();
    }

    public Produto getProdutoNovo() {
        return produtoNovo;
    }

    public void setProdutoNovo(Produto produtoNovo) {
        this.produtoNovo = produtoNovo;
    }

    public CCadastrarDoacaoProduto getController() {
        return controller;
    }

    public void setController(CCadastrarDoacaoProduto controller) {
        this.controller = controller;
    }

    public VBox getPaneMain() {
        return paneMain;
    }

    public void setPaneMain(VBox paneMain) {
        this.paneMain = paneMain;
    }

    public VBox getPaneProduto() {
        return paneProduto;
    }

    public void setPaneProduto(VBox paneProduto) {
        this.paneProduto = paneProduto;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public HBox getPaneButtonsBottom() {
        return paneButtonsBottom;
    }

    public void setPaneButtonsBottom(HBox paneButtonsBottom) {
        this.paneButtonsBottom = paneButtonsBottom;
    }

    public Label getLblProduto() {
        return lblProduto;
    }

    public void setLblProduto(Label lblProduto) {
        this.lblProduto = lblProduto;
    }

    public Label getLblCodigo() {
        return lblCodigo;
    }

    public void setLblCodigo(Label lblCodigo) {
        this.lblCodigo = lblCodigo;
    }

    public Label getLblTipo() {
        return lblTipo;
    }

    public void setLblTipo(Label lblTipo) {
        this.lblTipo = lblTipo;
    }

    public Label getLblMarca() {
        return lblMarca;
    }

    public void setLblMarca(Label lblMarca) {
        this.lblMarca = lblMarca;
    }

    public Label getLblValidade() {
        return lblValidade;
    }

    public void setLblValidade(Label lblValidade) {
        this.lblValidade = lblValidade;
    }

    public TextField getTxtCodigo() {
        return txtCodigo;
    }

    public void setTxtCodigo(TextField txtCodigo) {
        this.txtCodigo = txtCodigo;
    }

    public ComboBox<TipoProduto> getCbbTipo() {
        return cbbTipo;
    }

    public void setCbbTipo(ComboBox<TipoProduto> cbbTipo) {
        this.cbbTipo = cbbTipo;
    }

    public ComboBox<MarcaProduto> getCbbMarca() {
        return cbbMarca;
    }

    public void setCbbMarca(ComboBox<MarcaProduto> cbbMarca) {
        this.cbbMarca = cbbMarca;
    }

    public DatePicker getDpcValidade() {
        return dpcValidade;
    }

    public void setDpcValidade(DatePicker dpcValidade) {
        this.dpcValidade = dpcValidade;
    }

    public Button getBtnCancelar() {
        return btnCancelar;
    }

    public void setBtnCancelar(Button btnCancelar) {
        this.btnCancelar = btnCancelar;
    }

    public Button getBtnCadastrar() {
        return btnCadastrar;
    }

    public void setBtnCadastrar(Button btnCadastrar) {
        this.btnCadastrar = btnCadastrar;
    }
}
