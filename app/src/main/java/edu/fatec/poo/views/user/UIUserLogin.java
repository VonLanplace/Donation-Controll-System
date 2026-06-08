package edu.fatec.poo.views.user;

import atlantafx.base.theme.Styles;
import edu.fatec.poo.controllers.user.CUserLogin;
import edu.fatec.poo.model.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class UIUserLogin extends BorderPane {

    private CUserLogin controller;
    private VBox centralPane;
    private Label lblTelaLogin;
    private Label lblMessage;
    private TextField txtEmail;
    private PasswordField txtSenha;
    private Button btnLogin;

    public UIUserLogin(CUserLogin controller) {
        super();
        this.controller = controller;

        centralPane = new VBox(25);
        centralPane.setAlignment(Pos.CENTER);
        centralPane.setPadding(new Insets(40));
        centralPane.setMaxWidth(400);

        lblTelaLogin = new Label("Sistema de Doações");
        lblTelaLogin.getStyleClass().add(Styles.TITLE_2);

        lblMessage = new Label();
        lblMessage.textProperty().bindBidirectional(controller.mensagemProperty());
        lblMessage.getStyleClass().add(Styles.WARNING);

        txtEmail = new TextField();
        txtEmail.setPromptText("E-mail do usuário");
        txtEmail.setPrefHeight(40);
        txtEmail.textProperty().bindBidirectional(controller.emailProperty());

        txtSenha = new PasswordField();
        txtSenha.setPromptText("Senha");
        txtSenha.setPrefHeight(40);
        txtSenha.textProperty().bindBidirectional(controller.senhaProperty());

        btnLogin = new Button("Entrar");
        btnLogin.getStyleClass().addAll(Styles.LARGE, Styles.ACCENT);
        btnLogin.setPrefWidth(Double.MAX_VALUE);
        btnLogin.setDefaultButton(true);

        btnLogin.setOnAction(e -> {
            Usuario usuario = controller.login();
        });

        centralPane.getChildren().addAll(
                lblTelaLogin,
                new Separator(),
                lblMessage,
                txtEmail,
                txtSenha,
                btnLogin
        );

        VBox centerWrapper = new VBox(centralPane);
        centerWrapper.setAlignment(Pos.CENTER);

        this.setCenter(centerWrapper);
    }
}