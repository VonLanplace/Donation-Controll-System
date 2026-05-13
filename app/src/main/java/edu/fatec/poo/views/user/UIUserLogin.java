package edu.fatec.poo.views.user;

import edu.fatec.poo.controllers.user.CUserLogin;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static edu.fatec.poo.configs.WindowStandardFormatting.HEIGHT;
import static edu.fatec.poo.configs.WindowStandardFormatting.WHIDTH;

public class UIUserLogin extends Application {

    private CUserLogin controller;
    private BorderPane mainPane;
    private Scene scene;
    private VBox centralPane;
    private Label lblTelaLogin;
    private TextField txtEmail;
    private PasswordField txtSenha;
    private Button btnLogin;

    @Override
    public void start(Stage stage) throws Exception {
        controller = new CUserLogin();

        mainPane = new BorderPane();
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
            controller.login();
        });

        centralPane.getChildren().addAll(lblTelaLogin, txtEmail, txtSenha, btnLogin);
        mainPane.setCenter(centralPane);

        scene = new Scene(mainPane, WHIDTH, HEIGHT);
        Bindings.bindBidirectional(new ReadOnlyObjectWrapper<>(stage), controller.getStage());
        stage.setTitle("Sistema de Login");
        stage.setScene(scene);
        stage.show();
    }
}