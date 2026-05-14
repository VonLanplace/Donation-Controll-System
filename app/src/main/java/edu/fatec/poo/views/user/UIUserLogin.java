package edu.fatec.poo.views.user;

import atlantafx.base.theme.Styles;
import edu.fatec.poo.controllers.user.CUserLogin;
import edu.fatec.poo.model.Usuario;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class UIUserLogin extends BorderPane {

    private CUserLogin controller;
    private VBox centralPane;
    private Label lblTelaLogin;
    private TextField txtEmail;
    private PasswordField txtSenha;
    private Button btnLogin;

    public UIUserLogin(CUserLogin controller) {
        super();
        this.controller = controller;

        // Configuração do Painel Central
        centralPane = new VBox(25); // Aumentado o espaçamento entre elementos
        centralPane.setAlignment(Pos.CENTER);
        centralPane.setPadding(new Insets(40));
        centralPane.setMaxWidth(400); // Limita a largura do formulário

        // Título estilizado com AtlantaFX
        lblTelaLogin = new Label("Sistema de Doações");
        lblTelaLogin.getStyleClass().add(Styles.TITLE_2);

        // Campo de Email
        txtEmail = new TextField();
        txtEmail.setPromptText("E-mail do usuário");
        txtEmail.setPrefHeight(40); // Mais alto para facilitar o clique
        txtEmail.textProperty().bindBidirectional(controller.getEmail());

        // Campo de Senha
        txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha");
        txtSenha.setPrefHeight(40);
        txtSenha.textProperty().bindBidirectional(controller.getSenha());

        // Botão de Login (Sólido e com cor Accent/Nord)
        btnLogin = new Button("Entrar");
        btnLogin.getStyleClass().addAll(Styles.LARGE, Styles.ACCENT);
        btnLogin.setPrefWidth(Double.MAX_VALUE); // Botão ocupa a largura do formulário
        btnLogin.setDefaultButton(true);

        btnLogin.setOnAction(e -> {
            Usuario usuario = controller.login();
        });

        // Agrupando elementos
        centralPane.getChildren().addAll(
                lblTelaLogin,
                new Separator(), // Linha sutil para separar título do form
                txtEmail,
                txtSenha,
                btnLogin
        );

        // Centralização absoluta na tela
        VBox centerWrapper = new VBox(centralPane);
        centerWrapper.setAlignment(Pos.CENTER);

        this.setCenter(centerWrapper);
    }
}