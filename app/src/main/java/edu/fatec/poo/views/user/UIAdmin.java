package edu.fatec.poo.views.user;

import edu.fatec.poo.controllers.user.CAdmin;
import edu.fatec.poo.controllers.user.CUserLogin;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.util.Acesso;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.util.Stack;

import static edu.fatec.poo.configs.WindowStandardFormatting.HEIGHT;
import static edu.fatec.poo.configs.WindowStandardFormatting.WHIDTH;

@Getter
@Setter
public class UIAdmin extends BorderPane {

    private CAdmin controller;

    // Elementos Globais
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

        this.setPadding(new Insets(20));

        this.setTop(criarTabela());
        this.setCenter(criarPaneForm());
        this.setBottom(criarPaneBotoes());

        BorderPane.setMargin(tbvUsuario, new Insets(0, 0, 20, 0));
        BorderPane.setMargin(this.getCenter(), new Insets(20, 0, 20, 0));

        vincularPropriedades();
    }

    private Node criarTabela() {
        tbvUsuario = new TableView<>();
        tbvUsuario.setItems(controller.getUsuariosCadastrados());
        tbvUsuario.setPrefHeight(HEIGHT / 3);
        tbvUsuario.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getNome()));

        TableColumn<Usuario, Integer> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getTelefone()));

        TableColumn<Usuario, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(item -> new ReadOnlyObjectWrapper<>(item.getValue().getEmail()));

        tbvUsuario.getColumns().addAll(colNome, colTelefone, colEmail);
        return tbvUsuario;
    }

    private Node criarPaneForm() {
        GridPane paneFormulario = new GridPane();
        paneFormulario.setHgap(10);
        paneFormulario.setVgap(15);
        paneFormulario.setAlignment(Pos.CENTER);

        txtNome = new TextField();
        txtCpf = new TextField();
        txtTelefone = new TextField();
        txtEmail = new TextField();
        cbxAcesso = new ComboBox<>();
        cbxAcesso.getItems().addAll(Acesso.values());
        cbxAcesso.setMaxWidth(Double.MAX_VALUE);
        cbxAcesso = new ComboBox<>();
        cbxAcesso.getItems().addAll(Acesso.values());
        cbxAcesso.setMaxWidth(Double.MAX_VALUE);
        chkResetarSenha = new CheckBox("Resetar senha para o padrão");
        chkResetarSenha.setTooltip(new Tooltip("A senha será alterada para o CPF do usuário"));

        txtCpf.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));
        txtTelefone.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));

        paneFormulario.add(new Label("Nome:"), 0, 0);
        paneFormulario.add(txtNome, 1, 0);

        paneFormulario.add(new Label("CPF:"), 0, 1);
        paneFormulario.add(txtCpf, 1, 1);

        paneFormulario.add(new Label("Telefone:"), 0, 2);
        paneFormulario.add(txtTelefone, 1, 2);

        paneFormulario.add(new Label("Email:"), 0, 3);
        paneFormulario.add(txtEmail, 1, 3);

        paneFormulario.add(new Label("Nível Acesso:"), 0, 4);
        paneFormulario.add(cbxAcesso, 1, 4);

        paneFormulario.add(chkResetarSenha, 1, 5);

        ColumnConstraints col1 = new ColumnConstraints(100);
        ColumnConstraints col2 = new ColumnConstraints(250);
        paneFormulario.getColumnConstraints().addAll(col1, col2);

        return paneFormulario;
    }

    private Node criarPaneBotoes() {
        HBox paneButtons = new HBox(15); // espaçamento de 15px
        paneButtons.setAlignment(Pos.CENTER);

        btnSalvar = new Button("Salvar");
        btnSalvar.setPrefWidth(100);
        btnSalvar.setDefaultButton(true); // Aciona com Enter
        btnSalvar.setOnAction(event -> controller.salvar());

        btnDeletar = new Button("Deletar");
        btnDeletar.setPrefWidth(100);
        btnDeletar.setStyle("-fx-base: #ff6666;"); // Cor levemente avermelhada para alerta
        btnDeletar.setOnAction(event -> controller.deletar());

        btnLimpar = new Button("Limpar");
        btnLimpar.setPrefWidth(100);
        btnLimpar.setOnAction(event -> controller.limpar());

        btnSair = new Button("Voltar");
        btnSair.setPrefWidth(100);
        btnSair.setCancelButton(true); // Aciona com Esc
        btnSair.setOnAction(event -> controller.voltar());

        paneButtons.getChildren().addAll(btnSalvar, btnDeletar, btnLimpar, btnSair);
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