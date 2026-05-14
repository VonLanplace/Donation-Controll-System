package edu.fatec.poo.views.donation;

import edu.fatec.poo.controllers.donation.CCadastrarDoacao;
import edu.fatec.poo.model.produto.Produto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class UICadastrarDoacao extends GridPane {

    // Size Variables
    private static final double WHIDTH = 720;
    private static final double HEIGHT = 480;
    private static final double SPACING = 10;

    // Controller
    private CCadastrarDoacao controller;

    // Componentes da interface
    private HBox paneDoacao;
    private VBox paneProdutos;
    private HBox paneBotoes;

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

    public UICadastrarDoacao(CCadastrarDoacao controller) {
        super();

        // General Items
        this.controller = controller;

        // Pane Configs
        this.setPadding(new Insets(SPACING));

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
        this.getChildren().addAll(
                lblTituloDoacao,
                paneDoacao,
                lblTituloProdutos,
                paneProdutos,
                paneBotoes
        );

        this.controller.start();
    }

    private void configurarAreaDoacao() {
        paneDoacao = new HBox();
        paneDoacao.setPadding(new Insets(SPACING));
        paneDoacao.setSpacing(SPACING);

        VBox paneDoacaoNome = new VBox();
        lblNome = new Label("Nome Doador");
        txtNome = new TextField();
        txtNome.promptTextProperty().setValue("Digite o Nome do Doador.");
        txtNome.textProperty().bindBidirectional(controller.getNomeDoador());
        paneDoacaoNome.setSpacing(SPACING);
        paneDoacaoNome.getChildren().addAll(lblNome, txtNome);
        paneDoacaoNome.setPrefWidth((WHIDTH / 3) * 2);

        VBox paneDoacaoData = new VBox();
        lblData = new Label("Data Doação");
        dtpDataDoacao = new DatePicker();
        dtpDataDoacao.valueProperty().bindBidirectional(controller.getDate());
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
            controller.adicionar();
        });

        btnRemover = new Button("-");
        btnRemover.setOnAction(event -> {
            controller.remover();
        });

        paneProdutosBotoes.getChildren().addAll(btnRemover, btnAdicionar);

        // Configurar Tabela
        configurarAreaProdutoTabela();

        paneProdutos.getChildren().addAll(paneProdutosBotoes, tabelaProdutos);
    }

    private void configurarAreaProdutoTabela() {
        tabelaProdutos = new TableView<>();
        tabelaProdutos.setPlaceholder(new Label("Nenhum Produto adicionado."));
        tabelaProdutos.setItems(controller.getListaProdutos());

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

        tabelaProdutos.getColumns().add(colId);
        tabelaProdutos.getColumns().add(colNome);
        tabelaProdutos.getColumns().add(colTipo);
        tabelaProdutos.getColumns().add(colMarca);
        tabelaProdutos.getColumns().add(colValidade);
    }

    private void configurarAreaBotoesBase() {
        paneBotoes = new HBox();
        paneBotoes.setSpacing(SPACING);

        btnCadastrar = new Button("Cadastrar");
        btnCadastrar.setOnAction(event -> {
            controller.cadastrar();
        });

        btnCancelar = new Button("Cancelar");
        //TODO remove exit
        btnCancelar.setOnAction(event -> {
            controller.cancelar();
        });

        paneBotoes.getChildren().addAll(btnCancelar, btnCadastrar);
    }

}
