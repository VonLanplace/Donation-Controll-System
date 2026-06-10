package com.vonlanplace.doacao.controller;

import com.vonlanplace.doacao.springIntegration.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component // O Spring agora gerencia essa classe!
public class MainController {

    // Exemplo: Você pode injetar seus Repositories do H2 aqui normalmente!
    // @Autowired
    // private DoacaoRepository repository;

    private final StageManager stageManager;

    MainController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    public void handleButtonClick() {

        System.out.println("Botão clicado! O Controller é um Bean do Spring.");
        stageManager.switchScene("/CadastroPessoa.fxml", "Sistema de Doação - Principal");
    }

    @FXML
    public void handleCadastroDoacao(ActionEvent actionEvent) {
        stageManager.switchScene("/CadastroDoacao.fxml", "Sistema de Doação - Principal");
    }

    @FXML
    public void handleCadastroVisitante(ActionEvent actionEvent) {
        stageManager.switchScene("/CadastroPessoa.fxml", "Sistema de Doação - Principal");
    }
}