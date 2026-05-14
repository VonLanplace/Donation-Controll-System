package edu.fatec.poo.views.user;

import atlantafx.base.theme.Styles;
import edu.fatec.poo.controllers.user.CAdmin;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.util.Acesso;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UIAdmin extends BorderPane {

    private CAdmin controller;

    private TableView<Usuario> tbvUsuario;
    private TextField txtNome;
    private TextField txtTelefone;
    private TextField txtEmail;
    private TextField txtCpf;
    private ComboBox<Acesso> cbxAcesso;
    private CheckBox chkResetarSenha;

    private Button btnSalvar;
    private Button btnDeletar;
    private Button btnLimpar;
    private Button btnSair;

    public UIAdmin(CAdmin controller) {
        this.controller = controller;

        // Configuração do Painel Principal
        this.setPadding(new Insets(15));
        this.setPrefSize(720, 480);

        // Seções
        this.setTop(criarPaneTopo());
        this.setCenter(criarPaneForm());
        this.setBottom(criarPaneBotoes());

        // Ajustes de margem para não encostar nos componentes
        BorderPane.setMargin(this.getCenter(), new Insets(10, 0, 10, 0));

        vincularPropriedades();
    }

    private Node criarPaneTopo() {
        VBox containerTopo = new VBox(10);

        Label lblTitulo = new Label("Administração de Usuários");
        lblTitulo.getStyleClass().add(Styles.TITLE_3);

        tbvUsuario = new TableView<>();
        tbvUsuario.setItems(controller.getUsuariosCadastrados());
        // Altura fixa para garantir que o formulário apareça na tela 480p
        tbvUsuario.setPrefHeight(180);
        tbvUsuario.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tbvUsuario.getStyleClass().add(Styles.STRIPED);

        TableColumn<Usuario, String> colCpf = new TableColumn<>("CPF");
        colCpf.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getCpf()));
        colCpf.setMinWidth(130);
        colCpf.setPrefWidth(140);
        colCpf.setMaxWidth(160);

        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getNome()));

        TableColumn<Usuario, String> colAcesso = new TableColumn<>("Acesso");
        colAcesso.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getAcesso().name()));
        colAcesso.setPrefWidth(100);
        colAcesso.setMinWidth(colAcesso.getPrefWidth());
        colAcesso.setMaxWidth(colAcesso.getPrefWidth());

        TableColumn<Usuario, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getEmail()));

        tbvUsuario.getColumns().addAll(colAcesso, colCpf, colNome, colEmail);

        tbvUsuario.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> controller.select(newValue)
        );

        containerTopo.getChildren().addAll(lblTitulo, tbvUsuario);
        return containerTopo;
    }

    private Node criarPaneForm() {
        GridPane paneFormulario = new GridPane();
        paneFormulario.setHgap(15);
        paneFormulario.setVgap(10);
        paneFormulario.setAlignment(Pos.CENTER);
        paneFormulario.setPadding(new Insets(10));

        // Estilização leve para o formulário
        paneFormulario.getStyleClass().add(Styles.TEXT_SMALL);

        txtNome = new TextField();
        txtCpf = new TextField();
        txtTelefone = new TextField();
        txtEmail = new TextField();

        cbxAcesso = new ComboBox<>();
        cbxAcesso.getItems().addAll(Acesso.values());
        cbxAcesso.setMaxWidth(Double.MAX_VALUE);

        chkResetarSenha = new CheckBox("Resetar senha para 1234");
        chkResetarSenha.setTooltip(new Tooltip("A senha será alterada para o CPF do usuário"));

        // Labels com estilo
        paneFormulario.add(new Label("Nome:"), 0, 0);
        paneFormulario.add(txtNome, 1, 0);

        paneFormulario.add(new Label("CPF:"), 0, 1);
        paneFormulario.add(txtCpf, 1, 1);

        paneFormulario.add(new Label("Telefone:"), 2, 0); // Organizado em duas colunas para economizar altura
        paneFormulario.add(txtTelefone, 3, 0);

        paneFormulario.add(new Label("Email:"), 2, 1);
        paneFormulario.add(txtEmail, 3, 1);

        paneFormulario.add(new Label("Acesso:"), 0, 2);
        paneFormulario.add(cbxAcesso, 1, 2);

        paneFormulario.add(chkResetarSenha, 3, 2);

        // Restrições de coluna para os 720px
        ColumnConstraints cLabel = new ColumnConstraints(60);
        ColumnConstraints cField = new ColumnConstraints(200);
        paneFormulario.getColumnConstraints().addAll(cLabel, cField, cLabel, cField);

        return paneFormulario;
    }

    private Node criarPaneBotoes() {
        HBox paneButtons = new HBox(15);
        paneButtons.setAlignment(Pos.CENTER);
        paneButtons.setPadding(new Insets(10, 0, 0, 0));

        btnSalvar = new Button("Salvar");
        btnSalvar.getStyleClass().add(Styles.SUCCESS); // Verde sólido
        btnSalvar.setPrefWidth(110);
        btnSalvar.setDefaultButton(true);
        btnSalvar.setOnAction(event -> controller.salvar());

        btnDeletar = new Button("Deletar");
        btnDeletar.getStyleClass().addAll(Styles.BUTTON_OUTLINED, Styles.DANGER); // Borda vermelha
        btnDeletar.setPrefWidth(110);
        btnDeletar.setOnAction(event -> controller.deletar());

        btnLimpar = new Button("Limpar");
        btnLimpar.getStyleClass().add(Styles.FLAT);
        btnLimpar.setPrefWidth(100);
        btnLimpar.setOnAction(event -> controller.limpar());

        btnSair = new Button("Voltar");
        btnSair.getStyleClass().add(Styles.FLAT);
        btnSair.setPrefWidth(100);
        btnSair.setCancelButton(true);
        btnSair.setOnAction(event -> controller.voltar());

        paneButtons.getChildren().addAll(btnSair, btnLimpar, btnDeletar, btnSalvar);
        return paneButtons;
    }

    private void vincularPropriedades() {
        txtNome.textProperty().bindBidirectional(controller.getNome());
        txtCpf.textProperty().bindBidirectional(controller.getCpf());
        txtTelefone.textProperty().bindBidirectional(controller.getTelefone());
        txtEmail.textProperty().bindBidirectional(controller.getEmail());
        cbxAcesso.valueProperty().bindBidirectional(controller.getAcesso());
        chkResetarSenha.selectedProperty().bindBidirectional(controller.getResetarSenha());
    }
}