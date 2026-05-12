package edu.fatec.poo.views;

import edu.fatec.poo.controllers.CCadastrarPessoa;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

        Tab tabResidencia = new Tab("Dados Residênciais", buildPaneDadosResidenciais());
        tabResidencia.setClosable(false);

        tabPane.getTabs().addAll(tabPessoal, tabResidencia);

        Scene scene = new Scene(tabPane, WHIDTH, HEIGHT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Tela Pessoa");
        primaryStage.show();
    }

    private Node buildPaneDadosResidenciais() {
        VBox paneDadosPessoais = new VBox();
        paneDadosPessoais.setPadding(PADDING);
        paneDadosPessoais.setSpacing(SPACING);

        Label lblPessoal = new Label("Dados Residênciais");

        GridPane paneResidencial = new GridPane();

        paneResidencial.setVgap(SPACING);
        paneResidencial.setHgap(SPACING);
        paneResidencial.setPadding(new Insets(SPACING));

        TextField txtRua = new TextField();
        txtRua.setPromptText("Insira a Rua.");

        TextField txtCep = new TextField();
        txtCep.setPromptText("Insira o CEP.");

        TextField txtCidade = new TextField();
        txtCidade.setPromptText("Insira a Cidade.");

        TextField txtBairo = new TextField();
        txtBairo.setPromptText("Insira o Bairo.");

        TextField txtNum = new TextField();
        txtNum.setPromptText("Insira o Numero.");
        //TODO
        txtNum.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        }));

        TextField txtComplemento = new TextField();
        txtComplemento.setPromptText("Insira o Complemento.");

        paneResidencial.add(lblPessoal, 0, 0, 1, 1);
        paneResidencial.add(txtRua, 0, 1, 1, 1);
        paneResidencial.add(txtCep, 1, 1, 1, 1);
        paneResidencial.add(txtCidade, 0, 2, 1, 1);
        paneResidencial.add(txtBairo, 1, 2, 1, 1);
        paneResidencial.add(txtNum, 0, 3, 1, 1);
        paneResidencial.add(txtComplemento, 1, 3, 1, 1);

        paneDadosPessoais.getChildren().addAll(paneResidencial);
        return paneDadosPessoais;
    }

    private Node buildPaneDadosPessoais() {
        VBox paneDadosPessoais = new VBox();
        paneDadosPessoais.setPadding(PADDING);
        paneDadosPessoais.setSpacing(SPACING);

        Label lblPessoal = new Label("Dados");

        GridPane panePessoal = new GridPane();

        panePessoal.setVgap(SPACING);
        panePessoal.setHgap(SPACING);
        panePessoal.setPadding(new Insets(SPACING));

        TextField txtNome = new TextField();
        txtNome.setPromptText("Insira o Nome.");

        TextField txtSobrenome = new TextField();
        txtSobrenome.setPromptText("Insira o Sobrenome.");

        TextField txtCpf = new TextField();
        txtCpf.setPromptText("Insira o Cpf.");

        TextField txtRg = new TextField();
        txtRg.setPromptText("Insira o numero Rg.");

        DatePicker dtpNascimeto = new DatePicker();
        dtpNascimeto.setPromptText("Insira a Data de Nascimento.");

        CheckBox ckbLeEscreve = new CheckBox("Lê/Escreve");

        panePessoal.add(lblPessoal, 0, 0, 1, 1);
        panePessoal.add(txtNome, 0, 1, 1, 1);
        panePessoal.add(txtSobrenome, 1, 1, 1, 1);
        panePessoal.add(txtCpf, 0, 2, 1, 1);
        panePessoal.add(txtRg, 1, 2, 1, 1);
        panePessoal.add(dtpNascimeto, 0, 3, 1, 1);
        panePessoal.add(ckbLeEscreve, 1, 3, 1, 1);

        paneDadosPessoais.getChildren().addAll(panePessoal);

        GridPane paneContato = new GridPane();

        paneContato.setPadding(PADDING);
        paneContato.setHgap(SPACING);
        paneContato.setVgap(SPACING);

        Label lblContato = new Label("Contato");

        TextField txtTelefone = new TextField();
        txtTelefone.setPromptText("Insira o Telefone");

        TextField txtTelefoneContato = new TextField();
        txtTelefoneContato.setPromptText("Insira o Telefone para Contato");

        TextField txtEmail = new TextField();
        txtEmail.setPromptText("Insira o Email caso tenha");

        CheckBox ckbJuridico = new CheckBox("Necessita de Júridico");

        CheckBox ckbPsico = new CheckBox("Necessita de Psicológa");

        paneContato.add(lblContato, 0, 0, 1, 1);
        paneContato.add(txtTelefone, 0, 1, 1, 1);
        paneContato.add(txtTelefoneContato, 1, 1, 1, 1);
        paneContato.add(txtEmail, 0, 2, 2, 1);
        paneContato.add(ckbJuridico, 0, 3, 1, 1);
        paneContato.add(ckbPsico, 1, 3, 1, 1);

        paneDadosPessoais.getChildren().addAll(paneContato);
        return paneDadosPessoais;
    }
}
