package edu.fatec.poo.views;

import edu.fatec.poo.controllers.CCadastrarDoacaoProduto;
import edu.fatec.poo.entities.produto.MarcaProduto;
import edu.fatec.poo.entities.produto.Produto;
import edu.fatec.poo.entities.produto.TipoProduto;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UICadastarDoacaoProduto extends Application {

    // Size Variables
    private static final double WHIDTH = 360;
    private static final double HEIGHT = 360;
    private static final double SPACING = 10;

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

    private ComboBox<TipoProduto> cbxTipo;
    private ComboBox<MarcaProduto> cbxMarca;

    private DatePicker dpcValidade;

    private Button btnCancelar;
    private Button btnCadastrar;

    // TODO Use DTO
    Produto produtoNovo;

    @Override
    public void start(Stage stage) {
        controller = new CCadastrarDoacaoProduto();
        paneMain = new VBox();
        scene = new Scene(paneMain, WHIDTH, HEIGHT);

        // Stage Configs
        stage.setTitle("Cadastro de Doação");
        stage.setResizable(false);
        stage.setScene(scene);

        // Pane Configs
        paneMain.setPadding(new Insets(SPACING));
        paneMain.setSpacing(SPACING);

        lblProduto = new Label("Produto");

        paneProduto = new VBox();
        paneProduto.setPadding(new Insets(SPACING));
        paneProduto.setSpacing(SPACING);

        lblCodigo = new Label("Codigo de Barras");
        txtCodigo = new TextField();
        txtCodigo.promptTextProperty().setValue("O codigo de barras é opicional");
        txtCodigo.textProperty().bindBidirectional(controller.codigoProperty());

        lblTipo = new Label("Tipo");
        cbxTipo = new ComboBox<>();
        cbxTipo.valueProperty().bindBidirectional(controller.tipoProperty());

        lblMarca = new Label("Marca");
        cbxMarca = new ComboBox<>();
        cbxMarca.valueProperty().bindBidirectional(controller.marcaProperty());

        lblValidade = new Label("Validade");
        dpcValidade = new DatePicker(LocalDate.now());
        dpcValidade.valueProperty().bindBidirectional(controller.validadeProperty());

        paneProduto.getChildren().addAll(
                lblCodigo, txtCodigo,
                lblTipo, cbxTipo,
                lblMarca, cbxMarca,
                lblValidade, dpcValidade
        );

        paneButtonsBottom = new HBox();
        paneButtonsBottom.setSpacing(SPACING);

        btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(p -> {
            stage.getScene().getRoot().setDisable(true);
            try {
                stage.close();
            } finally {
                stage.getScene().getRoot().setDisable(false);
            }
        });

        btnCadastrar = new Button("Cadastrar");
        btnCadastrar.setOnAction(p -> {
            stage.getScene().getRoot().setDisable(true);
            try {
                produtoNovo = controller.cadastrar();
                stage.close();
            } finally {
                stage.getScene().getRoot().setDisable(false);
            }
        });

        paneButtonsBottom.getChildren().addAll(btnCancelar, btnCadastrar);

        paneMain.getChildren().addAll(
                lblProduto,
                paneProduto,
                paneButtonsBottom
        );

        controller.start();
        stage.showAndWait();
    }
}
