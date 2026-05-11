package edu.fatec.poo.views;

import edu.fatec.poo.controllers.CCadastrarPessoa;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.NoArgsConstructor;

import static edu.fatec.poo.configs.WindowStandardFormatting.*;

@NoArgsConstructor
public class UICadastrarPessoa extends Application {

    // Controller
    CCadastrarPessoa controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (this.controller == null) {
            this.controller = new CCadastrarPessoa();
        }

        TabPane tabPane = new TabPane();

        Tab tabPessoal = new Tab("Dados Pessoais", buildPaneDadosPessoais());
        tabPessoal.setClosable(false);

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
        paneDadosPessoais.setSpacing(SPACING);
        paneDadosPessoais.setSpacing(SPACING);

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

        VBox paneContato = new VBox();

        paneContato.setPadding(PADDING);
        paneContato.setSpacing(SPACING);

        Label lblContato = new Label("Contato");

        TextField txtTelefone = new TextField();
        txtTelefone.setPromptText("Insira o Telefone");

        TextField txtTelefoneContato = new TextField();
        txtTelefoneContato.setPromptText("Insira o Telefone para Contato");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Insira o Email caso tenha");

        CheckBox ckbJuridico = new CheckBox("Necessita de Júridico");

        CheckBox ckbPsico = new CheckBox("Necessita de Psicológa");

        paneContato.getChildren().addAll(
                txtTelefone,
                txtTelefoneContato,
                txtEmail,
                ckbJuridico,
                ckbPsico
        );

        paneDadosPessoais.getChildren().addAll(lblContato, paneContato);
        return paneDadosPessoais;
    }
}
