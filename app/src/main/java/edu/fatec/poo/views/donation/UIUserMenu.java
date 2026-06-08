package edu.fatec.poo.views.donation;

import atlantafx.base.theme.Styles;
import edu.fatec.poo.controllers.donation.CUserMenu;
import edu.fatec.poo.model.Doacao;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class UIUserMenu extends BorderPane {

    private final CUserMenu controller;

    public UIUserMenu(CUserMenu cUserMenu) {
        this.controller = cUserMenu;
        this.setPadding(new Insets(20));

        configSearch();
        configTabela();
        configButtonsBottom();
    }

    private void configSearch() {
        HBox paneTop = new HBox(10);
        paneTop.setAlignment(Pos.CENTER_LEFT);
        paneTop.setPadding(new Insets(0, 0, 15, 0));

        TextField txtPesquisa = new TextField();
        txtPesquisa.setPromptText("Insira o nome do Doador...");
        txtPesquisa.textProperty().bindBidirectional(controller.nomePesquisaProperty());
        HBox.setHgrow(txtPesquisa, Priority.ALWAYS);

        Button btnPesquisar = new Button("Pesquisar︎");
        btnPesquisar.getStyleClass().add(Styles.ACCENT);
        btnPesquisar.setPrefWidth(100);
        btnPesquisar.setDefaultButton(true);
        btnPesquisar.setOnAction(event -> controller.pesquisar());

        Button btnLimpar = new Button("Limpar");
        btnLimpar.getStyleClass().add(Styles.FLAT);
        btnLimpar.setPrefWidth(90);
        btnLimpar.setOnAction(event -> controller.limpar());

        paneTop.getChildren().addAll(txtPesquisa, btnLimpar, btnPesquisar);
        this.setTop(paneTop);
    }

    private void configButtonsBottom() {
        HBox paneBottom = new HBox(12);
        paneBottom.setAlignment(Pos.CENTER_RIGHT);
        paneBottom.setPadding(new Insets(15, 0, 0, 0));

        Button btnVoltar = new Button("Voltar");
        btnVoltar.getStyleClass().add(Styles.FLAT);
        btnVoltar.setPrefWidth(100);
        btnVoltar.setCancelButton(true);
        btnVoltar.setOnAction(event -> controller.voltar());

        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add(Styles.BUTTON_OUTLINED);
        btnEditar.setPrefWidth(100);
        btnEditar.setOnAction(event -> controller.editar());

        Button btnVer = new Button("Visualizar");
        btnVer.getStyleClass().add(Styles.BUTTON_OUTLINED);
        btnVer.setPrefWidth(110);
        btnVer.setOnAction(event -> controller.ver());

        Button btnNovo = new Button("Novo");
        btnNovo.getStyleClass().add(Styles.ACCENT);
        btnNovo.setPrefWidth(100);
        btnNovo.setOnAction(event -> controller.novo());

        paneBottom.getChildren().addAll(btnVoltar, new javafx.scene.layout.Region(), btnEditar, btnVer, btnNovo);
        HBox.setHgrow(paneBottom.getChildren().get(1), Priority.ALWAYS);

        this.setBottom(paneBottom);
    }

    public void configTabela() {
        TableView<Doacao> tbvDoacao = new TableView<>();
        tbvDoacao.setItems(controller.getDoacaos());
        tbvDoacao.setPlaceholder(new Label("Nenhuma doação encontrada."));
        tbvDoacao.getSelectionModel().selectedItemProperty().addListener(
                (doacao, velho, novo) -> controller.doacaoSelecionadaProperty().setValue(novo)
        );

        tbvDoacao.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tbvDoacao.getStyleClass().addAll(Styles.STRIPED, Styles.BORDERED); // Bordas sutis combinam com Nord

        TableColumn<Doacao, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(doacao
                -> new ReadOnlyObjectWrapper<>(doacao.getValue().getDataValidadeDdMmYyyy())
        );
        colData.setMaxWidth(1200);

        TableColumn<Doacao, String> colNome = new TableColumn<>("Doador");
        colNome.setCellValueFactory(doacao
                -> new ReadOnlyObjectWrapper<>(doacao.getValue().getNomeDoador())
        );

        TableColumn<Doacao, String> colCadastrante = new TableColumn<>("Cadastrante");
        colCadastrante.setCellValueFactory(doacao
                -> new ReadOnlyObjectWrapper<>(doacao.getValue().getCadastrante().getNome())
        );

        tbvDoacao.getColumns().add(colData);
        tbvDoacao.getColumns().add(colNome);
        tbvDoacao.getColumns().add(colCadastrante);

        this.setCenter(tbvDoacao);
    }

    public CUserMenu getController() {
        return controller;
    }
}