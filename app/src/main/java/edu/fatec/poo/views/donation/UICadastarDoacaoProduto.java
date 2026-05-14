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

        VBox boxTipo = new VBox(5, new Label("Tipo"), cbxTipo = new ComboBox<>());
        cbxTipo.setMaxWidth(Double.MAX_VALUE);
        cbxTipo.valueProperty().bindBidirectional(controller.tipoProperty());

        VBox boxMarca = new VBox(5, new Label("Marca"), cbxMarca = new ComboBox<>());
        cbxMarca.setMaxWidth(Double.MAX_VALUE);
        cbxMarca.valueProperty().bindBidirectional(controller.marcaProperty());

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
        btnCadastrar.getStyleClass().add(Styles.ACCENT); // Azul Nord
        btnCadastrar.setPrefWidth(100);
        btnCadastrar.setDefaultButton(true);
        btnCadastrar.setOnAction(p -> {
            produtoNovo = controller.cadastrar();
            stage.close();
        });

        paneButtonsBottom.getChildren().addAll(btnCancelar, btnCadastrar);

        // Montagem final
        paneMain.getChildren().addAll(lblProduto, new Separator(), paneProduto, paneButtonsBottom);

        stage.setTitle("Cadastro de Doação");
        stage.setResizable(false);
        stage.setScene(scene);

        controller.start();
        stage.showAndWait();
    }
}
