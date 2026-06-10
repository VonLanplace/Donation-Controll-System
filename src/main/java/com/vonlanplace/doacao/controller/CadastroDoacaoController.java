package com.vonlanplace.doacao.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class CadastroDoacaoController {

    @FXML
    public void handleRemoverProduto(ActionEvent actionEvent) {
        System.out.println("Remover Produto");
    }

    @FXML
    public void handleAdicionarProduto(ActionEvent actionEvent) {
        System.out.println("Adicionar Produto");
    }

    @FXML
    public void handleDeletar(ActionEvent actionEvent) {
        System.out.println("Deletar Doacao");
    }

    @FXML
    public void handleCancelar(ActionEvent actionEvent) {
        System.out.println("Cancelar Doacao");
    }

    @FXML
    public void handleFinalizarCadastro(ActionEvent actionEvent) {
        System.out.println("Finalizar Doacao");
    }
}
