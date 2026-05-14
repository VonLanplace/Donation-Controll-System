package edu.fatec.poo.views.user;

import edu.fatec.poo.controllers.AController;
import edu.fatec.poo.controllers.user.CUserLogin;
import edu.fatec.poo.exceptions.InvalidControllerException;
import edu.fatec.poo.model.Usuario;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

        centralPane = new VBox(20);
        centralPane.setAlignment(Pos.CENTER);
        centralPane.setPadding(new Insets(50));

        lblTelaLogin = new Label("Login");
        lblTelaLogin.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        txtEmail = new TextField();
        txtEmail.setPromptText("Insira o email.");
        txtEmail.setMaxWidth(300);
        txtEmail.setStyle("-fx-font-size: 16px;");
        txtEmail.textProperty().bindBidirectional(controller.getEmail());

        txtSenha = new PasswordField();
        txtSenha.setPromptText("Insira a senha.");
        txtSenha.setMaxWidth(300);
        txtSenha.setStyle("-fx-font-size: 16px;");
        txtSenha.textProperty().bindBidirectional(controller.getSenha());

        btnLogin = new Button("Entrar");
        btnLogin.setPrefWidth(120);
        btnLogin.setStyle("-fx-font-size: 16px; -fx-base: #2196F3; -fx-text-fill: white;");

        btnLogin.setOnAction(e -> {
            Usuario usuario = controller.login();
        });

        centralPane.getChildren().addAll(lblTelaLogin, txtEmail, txtSenha, btnLogin);
        this.setCenter(centralPane);
    }
}