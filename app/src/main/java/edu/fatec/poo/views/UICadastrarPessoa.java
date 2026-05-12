package edu.fatec.poo.views;

import edu.fatec.poo.controllers.CCadastrarPessoa;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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
        paneDadosPessoais.setPadding(new Insets(SPACING));

        // --- CONFIGURAÇÃO DE COLUNAS PARA OS GRIDS ---
        // Criamos duas restrições de 50% de largura cada
        ColumnConstraints col50 = new ColumnConstraints();
        col50.setPercentWidth(50);
        col50.setHgrow(Priority.ALWAYS);

        // --- GRID PESSOAL ---
        GridPane panePessoal = new GridPane();
        panePessoal.setVgap(SPACING);
        panePessoal.setHgap(SPACING);
        // Aplica as colunas de 50%
        panePessoal.getColumnConstraints().addAll(col50, col50);

        Label lblPessoal = new Label("Dados");

        TextField txtNome = new TextField();
        txtNome.setPromptText("Insira o Nome.");
        txtNome.setMaxWidth(Double.MAX_VALUE); // Faz esticar na célula

        TextField txtSobrenome = new TextField();
        txtSobrenome.setPromptText("Insira o Sobrenome.");
        txtSobrenome.setMaxWidth(Double.MAX_VALUE);

        TextField txtCpf = new TextField();
        txtCpf.setPromptText("Insira o Cpf.");
        txtCpf.setMaxWidth(Double.MAX_VALUE);

        TextField txtRg = new TextField();
        txtRg.setPromptText("Insira o numero Rg.");
        txtRg.setMaxWidth(Double.MAX_VALUE);

        DatePicker dtpNascimeto = new DatePicker();
        dtpNascimeto.setPromptText("Insira a Data de Nascimento.");
        dtpNascimeto.setMaxWidth(Double.MAX_VALUE); // DatePicker também precisa disso

        CheckBox ckbLeEscreve = new CheckBox("Lê/Escreve");

        panePessoal.add(lblPessoal, 0, 0, 2, 1); // Span de 2 colunas para o título
        panePessoal.add(txtNome, 0, 1);
        panePessoal.add(txtSobrenome, 1, 1);
        panePessoal.add(txtCpf, 0, 2);
        panePessoal.add(txtRg, 1, 2);
        panePessoal.add(dtpNascimeto, 0, 3);
        panePessoal.add(ckbLeEscreve, 1, 3);

        // --- GRID CONTATO ---
        GridPane paneContato = new GridPane();
        paneContato.setHgap(SPACING);
        paneContato.setVgap(SPACING);
        // Reaproveita as mesmas restrições de coluna
        paneContato.getColumnConstraints().addAll(col50, col50);

        Label lblContato = new Label("Contato");

        TextField txtTelefone = new TextField();
        txtTelefone.setPromptText("Insira o Telefone");
        txtTelefone.setMaxWidth(Double.MAX_VALUE);

        TextField txtTelefoneContato = new TextField();
        txtTelefoneContato.setPromptText("Insira o Telefone para Contato");
        txtTelefoneContato.setMaxWidth(Double.MAX_VALUE);

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Insira o Email caso tenha");
        txtEmail.setMaxWidth(Double.MAX_VALUE);

        CheckBox ckbJuridico = new CheckBox("Necessita de Júridico");
        CheckBox ckbPsico = new CheckBox("Necessita de Psicológa");

        paneContato.add(lblContato, 0, 0, 2, 1); // Span de 2 colunas
        paneContato.add(txtTelefone, 0, 1);
        paneContato.add(txtTelefoneContato, 1, 1);
        paneContato.add(txtEmail, 0, 2, 2, 1); // Email ocupando as duas colunas
        paneContato.add(ckbJuridico, 0, 3);
        paneContato.add(ckbPsico, 1, 3);

        paneDadosPessoais.getChildren().addAll(panePessoal, paneContato);

        return paneDadosPessoais;
    }
}
