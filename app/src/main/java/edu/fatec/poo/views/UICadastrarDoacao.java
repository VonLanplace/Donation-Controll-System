package edu.fatec.poo.views;

import atlantafx.base.theme.NordDark;

import edu.fatec.poo.controllers.CCadastrarDoacao;
import edu.fatec.poo.entities.produto.Produto;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class UICadastrarDoacao extends Application {

    // Size Variables
    private static final double WHIDTH = 720;
    private static final double HEIGHT = 480;
    private static final double SPACING = 10;

    // Controller
    private CCadastrarDoacao controll;

    // Componentes da interface
    private VBox paneMain;
    private HBox paneDoacao;
    private VBox paneProdutos;
    private HBox paneBotoes;

    private Scene scene;
    private Stage stage;

    // Labels
    private Label lblNome;
    private Label lblData;
    private Label lblTituloDoacao;
    private Label lblTituloProdutos;

    // TextFields
    private TextField txtNome;

    // DatePicker
    private DatePicker dtpDataDoacao;

    // Tabelas
    // TODO Use the DTOProdutoLer
    private TableView<Produto> tabelaProdutos;
    private TableColumn<Produto, Long> colId;
    private TableColumn<Produto, String> colNome;
    private TableColumn<Produto, String> colMarca;
    private TableColumn<Produto, String> colTipo;
    private TableColumn<Produto, LocalDate> colValidade;

    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    // Buttons
    private Button btnAdicionar;
    private Button btnRemover;
    private Button btnCadastrar;
    private Button btnCancelar;

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());

        // General Items
        controll = new CCadastrarDoacao();
        this.stage = stage;
        paneMain = new VBox();
        scene = new Scene(paneMain, WHIDTH, HEIGHT);

        // Stage Configs
        stage.setTitle("Cadastro de Doação");
        stage.setResizable(false);
        stage.setScene(scene);

        // Pane Configs
        paneMain.setPadding(new Insets(SPACING));
        paneMain.setSpacing(SPACING);

        // Itens

        // Area Doação
        lblTituloDoacao = new Label("Doação");
        configurarAreaDoacao();

        // Area Produtos
        lblTituloProdutos = new Label("Produtos");
        configurarAreaProdutos();

        // Botoes finais
        configurarAreaBotoesBase();

        // Initiation
        paneMain.getChildren().addAll(
                lblTituloDoacao,
                paneDoacao,
                lblTituloProdutos,
                paneProdutos,
                paneBotoes
        );

        controll.start();
        stage.show();
    }

    private void configurarAreaDoacao() {
        paneDoacao = new HBox();
        paneDoacao.setPadding(new Insets(SPACING));
        paneDoacao.setSpacing(SPACING);

        VBox paneDoacaoNome = new VBox();
        lblNome = new Label("Nome Doador");
        txtNome = new TextField();
        txtNome.promptTextProperty().setValue("Digite o Nome do Doador.");
        txtNome.textProperty().bindBidirectional(controll.nomeDoadorProperty());
        paneDoacaoNome.setSpacing(SPACING);
        paneDoacaoNome.getChildren().addAll(lblNome, txtNome);
        paneDoacaoNome.setPrefWidth((WHIDTH / 3) * 2);

        VBox paneDoacaoData = new VBox();
        lblData = new Label("Data Doação");
        dtpDataDoacao = new DatePicker();
        dtpDataDoacao.valueProperty().bindBidirectional(controll.dateProperty());
        paneDoacaoData.setSpacing(SPACING);
        paneDoacaoData.getChildren().addAll(lblData, dtpDataDoacao);

        paneDoacao.getChildren().addAll(
                paneDoacaoNome, paneDoacaoData
        );
    }

    private void configurarAreaProdutos() {
        paneProdutos = new VBox();
        paneProdutos.setPadding(new Insets(SPACING));
        paneProdutos.setSpacing(SPACING);

        HBox paneProdutosBotoes = new HBox();
        paneProdutosBotoes.setSpacing(SPACING);

        btnAdicionar = new Button("+");
        btnAdicionar.setOnAction(event -> {
            stage.getScene().getRoot().setDisable(true);
            try {
                controll.adicionar();
            } finally {
                stage.getScene().getRoot().setDisable(false);
            }
        });

        btnRemover = new Button("-");
        btnRemover.setOnAction(event -> {
            stage.getScene().getRoot().setDisable(true);
            try {
                controll.remover();
            } finally {
                stage.getScene().getRoot().setDisable(false);
            }
        });

        paneProdutosBotoes.getChildren().addAll(btnRemover, btnAdicionar);

        // Configurar Tabela
        configurarAreaProdutoTabela();

        paneProdutos.getChildren().addAll(paneProdutosBotoes, tabelaProdutos);
    }

    private void configurarAreaProdutoTabela() {
        tabelaProdutos = new TableView<>();
        tabelaProdutos.setPlaceholder(new Label("Nenhum Produto adicionado."));
        tabelaProdutos.setItems(controll.getListaProdutos());
        controll.produtoSelecionadoProperty().bind(
                tabelaProdutos.getSelectionModel().selectedItemProperty()
        );

        colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        colMarca = new TableColumn<>("Marca");
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));

        colValidade = new TableColumn<>("Validade");
        colValidade.setCellValueFactory(new PropertyValueFactory<>("validade"));

        tabelaProdutos.getColumns().addAll(colId, colNome, colTipo, colMarca, colValidade);
    }

    private void configurarAreaBotoesBase() {
        paneBotoes = new HBox();
        paneBotoes.setSpacing(SPACING);

        btnCadastrar = new Button("Cadastrar");
        btnCadastrar.setOnAction(event -> {
            stage.getScene().getRoot().setDisable(true);
            try {
                controll.cadastrar();
            } finally {
                stage.getScene().getRoot().setDisable(false);
            }
        });

        btnCancelar = new Button("Cancelar");
        //TODO remove exit
        btnCancelar.setOnAction(event -> {
            stage.getScene().getRoot().setDisable(true);
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(stage); // Keeps the window tied to the app
                alert.setTitle("Confirmação");
                alert.setHeaderText("Cancelar Ação");
                alert.setContentText("Tem certeza que deseja sair? Dados não salvos serão perdidos.");

                // Using ifPresent for cleaner syntax
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        stage.close();
                    }
                });
            } finally {
                stage.getScene().getRoot().setDisable(false);
            }
        });

        paneBotoes.getChildren().addAll(btnCancelar, btnCadastrar);
    }

}
