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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class UICadastarDoacaoProduto extends Application {

    // Size Variables
    private static final double WHIDTH = 720;
    private static final double HEIGHT = 480;
    private static final double SPACING = 10;

    private CCadastrarDoacaoProduto controller;

    private VBox paneMain;
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
    Produto produto = new Produto((long) ((Math.random() * (100 - 5)) + 5), 10L, 10L, 10L, UUID.randomUUID().toString());

    @Override
    public void start(Stage stage) throws Exception {
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
        VBox paneProduto = new VBox();
        paneProduto.setPadding(new Insets(SPACING));
        paneProduto.setSpacing(SPACING);

        lblCodigo = new Label("Codigo de Barras");
        txtCodigo = new TextField();
        txtCodigo.promptTextProperty().setValue("O codigo de barras é opicional");

        lblTipo = new Label("Tipo");
        cbxTipo = new ComboBox<>();

        lblMarca = new Label("Marca");
        cbxMarca = new ComboBox<>();

        lblValidade = new Label("Validade");
        dpcValidade = new DatePicker(LocalDate.now());

        paneProduto.getChildren().addAll(
                lblCodigo, txtCodigo,
                lblTipo, cbxTipo,
                lblMarca, cbxMarca,
                lblValidade, dpcValidade
        );

        paneButtonsBottom = new HBox();
        paneButtonsBottom.setSpacing(SPACING);

        btnCancelar = new Button("Cancelar");
        btnCadastrar = new Button("Cadastrar");
        paneButtonsBottom.getChildren().addAll(btnCancelar, btnCadastrar);

        paneMain.getChildren().addAll(
                lblProduto,
                paneProduto,
                paneButtonsBottom

        );

        stage.showAndWait();
    }
}
