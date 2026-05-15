package edu.fatec.poo.views.donation;

import atlantafx.base.theme.Styles;
import edu.fatec.poo.controllers.donation.CCadastrarDoacao;
import edu.fatec.poo.model.produto.Produto;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class UICadastrarDoacao extends GridPane {

    private static final double SPACING = 15;
    private final CCadastrarDoacao controller;

    // Componentes de Layout
    private VBox paneDoacao;
    private VBox paneProdutos;
    private HBox paneBotoes;

    // Tabelas
    private TableView<Produto> tabelaProdutos;

    public UICadastrarDoacao(CCadastrarDoacao controller) {
        super();
        this.controller = controller;

        // Configuração do Grid Principal
        this.setPadding(new Insets(20));
        this.setHgap(SPACING);
        this.setVgap(SPACING);
        this.setAlignment(Pos.TOP_CENTER);

        // Criar as seções
        configurarAreaDoacao();
        configurarAreaProdutos();
        configurarAreaBotoesBase();

        // Posicionamento no Grid (coluna, linha)
        Label lblTitulo = new Label("Nova Doação");
        lblTitulo.getStyleClass().add(Styles.TITLE_3);

        this.add(lblTitulo, 0, 0);
        this.add(paneDoacao, 0, 1);
        this.add(paneProdutos, 0, 2);
        this.add(paneBotoes, 0, 3);

        // Ajuste para que a interface cresça horizontalmente
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHgrow(Priority.ALWAYS);
        this.getColumnConstraints().add(constraints);
    }


    private void configurarAreaDoacao() {
        paneDoacao = new VBox(10);

        Label lblTituloDoacao = new Label("Informações do Doador");
        lblTituloDoacao.getStyleClass().add(Styles.TITLE_4);

        HBox campos = new HBox(SPACING);

        VBox boxNome = new VBox(5);
        Label lblNome = new Label("Nome Doador");
        TextField txtNome = new TextField();
        txtNome.setPromptText("Digite o nome completo");
        txtNome.textProperty().bindBidirectional(controller.getNomeDoador());
        txtNome.setDisable(!controller.isEditavel());
        HBox.setHgrow(boxNome, Priority.ALWAYS);
        boxNome.getChildren().addAll(lblNome, txtNome);

        VBox boxData = new VBox(5);
        Label lblData = new Label("Data");
        DatePicker dtpDataDoacao = new DatePicker(LocalDate.now());
        dtpDataDoacao.valueProperty().bindBidirectional(controller.getDate());
        dtpDataDoacao.setDisable(!controller.isEditavel());
        boxData.getChildren().addAll(lblData, dtpDataDoacao);

        campos.getChildren().addAll(boxNome, boxData);
        paneDoacao.getChildren().addAll(lblTituloDoacao, campos);
    }

    private void configurarAreaProdutos() {
        paneProdutos = new VBox(10);
        VBox.setVgrow(paneProdutos, Priority.ALWAYS);

        HBox headerProdutos = new HBox();
        Label lblTituloProdutos = new Label("Itens da Doação");
        lblTituloProdutos.getStyleClass().add(Styles.TITLE_4);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox paneProdutosBotoes = new HBox(8);
        Button btnAdicionar = new Button("Adicionar Produto");
        btnAdicionar.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.ACCENT);
        btnAdicionar.setOnAction(event -> controller.adicionar());
        btnAdicionar.setVisible(controller.isEditavel());

        Button btnRemover = new Button("Remover");
        btnRemover.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER);
        btnRemover.setOnAction(event -> controller.remover());
        btnRemover.setVisible(controller.isEditavel());

        paneProdutosBotoes.getChildren().addAll(btnRemover, btnAdicionar);
        headerProdutos.getChildren().addAll(lblTituloProdutos, spacer, paneProdutosBotoes);

        configurarAreaProdutoTabela();
        paneProdutos.getChildren().addAll(headerProdutos, tabelaProdutos);
    }

    private void configurarAreaProdutoTabela() {
        tabelaProdutos = new TableView<>();
        tabelaProdutos.setPlaceholder(new Label("Nenhum produto na lista."));
        tabelaProdutos.setItems(controller.getListaProdutos());
        tabelaProdutos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        controller.getProdutoSelecionado().bind(
                tabelaProdutos.getSelectionModel().selectedItemProperty()
        );

        TableColumn<Produto, String> colCodigo = new TableColumn<>("Codigo de Barras");
        colCodigo.setCellValueFactory(produto ->
                new ReadOnlyObjectWrapper<>(produto.getValue().getCodigoBarras()));
        colCodigo.setMinWidth(140);
        colCodigo.setPrefWidth(160);
        colCodigo.setMaxWidth(200);

        TableColumn<Produto, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(produto ->
                new ReadOnlyObjectWrapper<>(produto.getValue().getTipo().getNome()));

        TableColumn<Produto, String> colMarca = new TableColumn<>("Marca");
        colMarca.setCellValueFactory(produto ->
                new ReadOnlyObjectWrapper<>(produto.getValue().getMarca().getNome()));

        TableColumn<Produto, String> colValidade = new TableColumn<>("Validade");
        colValidade.setCellValueFactory(produto ->
                new ReadOnlyObjectWrapper<>(produto.getValue().getDataValidadeDdMmYyyy()));

        tabelaProdutos.getColumns().add(colCodigo);
        tabelaProdutos.getColumns().add(colTipo);
        tabelaProdutos.getColumns().add(colMarca);
        tabelaProdutos.getColumns().add(colValidade);
    }

    private void configurarAreaBotoesBase() {
        paneBotoes = new HBox(15);
        paneBotoes.setAlignment(Pos.CENTER_RIGHT);
        paneBotoes.setPadding(new Insets(10, 0, 0, 0));

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setPrefWidth(120);
        btnCancelar.setOnAction(event -> controller.cancelar());
        btnCancelar.setVisible(controller.isEditavel());

        Button btnCadastrar = new Button();
        btnCadastrar.getStyleClass().addAll(Styles.SUCCESS, Styles.SUCCESS);
        btnCadastrar.setPrefWidth(160);
        if (controller.isEditavel()) {
            btnCadastrar.setText("Finalizar Cadastro");
            btnCadastrar.setOnAction(event -> controller.cadastrar());
        } else {
            btnCadastrar.setText("Voltar");
            btnCadastrar.setOnAction(event -> controller.voltar());
        }

        paneBotoes.getChildren().addAll(btnCancelar, btnCadastrar);
    }
}