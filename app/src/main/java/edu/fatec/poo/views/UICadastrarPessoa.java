package edu.fatec.poo.views;

import edu.fatec.poo.controllers.CCadastrarPessoa;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UICadastrarPessoa extends Application {

    // Size Variables
    private static final double WHIDTH = 720;
    private static final double HEIGHT = 480;
    private static final double SPACING = 10;

    // Controller
    CCadastrarPessoa controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (this.controller == null) {
            this.controller = new CCadastrarPessoa();
        }

        TabPane tabPane = new TabPane();

        Tab tabPessoal = new Tab("Dados Pessoais", buildPaneDadosPessoais());

        for (Tab t : tabPane.getTabs()) {
            t.setClosable(false);
        }

        tabPane.getTabs().addAll(tabPessoal);

        Scene scene = new Scene(tabPane, WHIDTH, HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tela Pessoa");
        primaryStage.show();
    }

    private Node buildPaneDadosPessoais() {
        VBox paneDadosPessoais = new VBox();

        Label lblPessoal = new Label("Dados");

        VBox panePessoal = new VBox();

        panePessoal.setSpacing(SPACING);
        panePessoal.setPadding(new Insets(SPACING));


        TextField txtNome = new TextField();
        txtNome.setPromptText("Insira o Nome.");

        TextField txtSobrenome = new TextField();
        txtSobrenome.setPromptText("Insira o Sobrenome.");

        TextField txtCpf = new TextField();
        txtCpf.setPromptText("Insira o Cpf.");

        TextField txtRg = new TextField();
        txtRg.setPromptText("Insira o numero Rg.");

        CheckBox ckbLeEscreve = new CheckBox("Lê/Escreve");

        panePessoal.getChildren().addAll(
                txtNome,
                txtSobrenome,
                txtCpf,
                txtRg,
                ckbLeEscreve
        );

        paneDadosPessoais.getChildren().addAll(lblPessoal, panePessoal);


        Pane paneContato = new Pane();
        Label lblContato = new Label("Contato");
        paneDadosPessoais.getChildren().addAll(lblContato, paneContato);
        return paneDadosPessoais;
    }
}
