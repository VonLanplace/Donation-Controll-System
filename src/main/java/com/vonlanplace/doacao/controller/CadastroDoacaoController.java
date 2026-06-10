package com.vonlanplace.doacao.controller;

import com.vonlanplace.doacao.springIntegration.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class CadastroDoacaoController {

    private final StageManager stageManager;

    CadastroDoacaoController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    public void handleRemoverProduto(ActionEvent actionEvent) {
        System.out.println("Remover Produto");
    }

    @FXML
    public void handleAdicionarProduto(ActionEvent actionEvent) {
        System.out.println("Adicionar Produto");
        stageManager.openModal("/CadastroProdutoDoacao.fxml", "Sistema de Doação - Principal");
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
