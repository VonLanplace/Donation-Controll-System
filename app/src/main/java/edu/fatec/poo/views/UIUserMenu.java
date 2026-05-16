package edu.fatec.poo.views;

import atlantafx.base.theme.Styles;
import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.Usuario;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UIUserMenu extends BorderPane {

    private final CUserMenu controller;

    public UIUserMenu(CUserMenu cUserMenu) {
        this.controller = cUserMenu;
        configSearch();
        configTabela();
        configButtonsBottom();
    }

    private void configSearch() {
        HBox paneTop = new HBox(15);
        paneTop.setAlignment(Pos.CENTER);
        paneTop.setPadding(new Insets(10, 0, 0, 0));

        TextField txtPesquisa = new TextField();
        txtPesquisa.setPromptText("Insira o nome do Doador");
        txtPesquisa.textProperty().bindBidirectional(controller.getNomePesquisa());

        Button btnPesquisar = new Button("\uD83D\uDD0D\uFE0E");
        btnPesquisar.getStyleClass().add(Styles.SUCCESS); // Verde sólido
        btnPesquisar.setPrefWidth(110);
        btnPesquisar.setDefaultButton(true);
        btnPesquisar.setOnAction(event -> controller.pesquisar());

        Button btnLimpar = new Button("Limpar");
        btnLimpar.getStyleClass().add(Styles.FLAT);
        btnLimpar.setPrefWidth(100);
        btnLimpar.setCancelButton(true);
        btnLimpar.setOnAction(event -> controller.limpar());

        paneTop.getChildren().addAll(txtPesquisa, btnLimpar, btnPesquisar);
        this.setTop(paneTop);
    }

    private void configButtonsBottom() {
        HBox paneBottom = new HBox(15);
        paneBottom.setAlignment(Pos.CENTER);
        paneBottom.setPadding(new Insets(10, 0, 0, 0));

        Button btnNovo = new Button("Novo");
        btnNovo.getStyleClass().add(Styles.SUCCESS); // Verde sólido
        btnNovo.setPrefWidth(110);
        btnNovo.setDefaultButton(true);
        btnNovo.setOnAction(event -> controller.novo());

        Button btnVer = new Button("Ver");
        btnVer.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER); // Borda vermelha
        btnVer.setPrefWidth(110);
        btnVer.setOnAction(event -> controller.ver());


        Button btnEditar = new Button("Editar");
        btnEditar.getStyleClass().add(Styles.FLAT);
        btnEditar.setPrefWidth(100);
        btnEditar.setOnAction(event -> controller.editar());

        Button btnVoltar = new Button("Voltar");
        btnVoltar.getStyleClass().add(Styles.FLAT);
        btnVoltar.setPrefWidth(100);
        btnVoltar.setCancelButton(true);
        btnVoltar.setOnAction(event -> controller.voltar());

        paneBottom.getChildren().addAll(btnVoltar, btnEditar, btnVer, btnNovo);
        this.setBottom(paneBottom);
    }

    public void configTabela() {
        TableView<Doacao> tbvDoacao = new TableView<>();
        tbvDoacao.setItems(controller.getDoacaos());
        tbvDoacao.getSelectionModel().selectedItemProperty().addListener(
                (doacao, velho, novo) ->
                        controller.getDoacaoSelecionada().setValue(novo)
        );
        tbvDoacao.setPrefHeight(180);
        tbvDoacao.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tbvDoacao.getStyleClass().add(Styles.STRIPED);

        TableColumn<Doacao, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(docaco
                -> new ReadOnlyObjectWrapper<>(docaco.getValue().getDataValidadeDdMmYyyy()
        ));
        TableColumn<Doacao, String> colNome = new TableColumn<>("Doador");
        colData.setCellValueFactory(docaco
                -> new ReadOnlyObjectWrapper<>(docaco.getValue().getNomeDoador()
        ));
        TableColumn<Doacao, String> colCadastrante = new TableColumn<>("Cadastrante");
        colData.setCellValueFactory(docaco
                -> new ReadOnlyObjectWrapper<>(docaco.getValue().getCadastrante().getNome()
        ));

        tbvDoacao.getColumns().add(colData);
        tbvDoacao.getColumns().add(colNome);
        tbvDoacao.getColumns().add(colCadastrante);

        this.setCenter(tbvDoacao);
    }
}
