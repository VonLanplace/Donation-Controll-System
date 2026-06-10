package com.vonlanplace.doacao.springIntegration;

import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    private final StageManager stageManager;

    // Injeta o gerenciador que criamos
    public PrimaryStageInitializer(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();

        // Passa a janela principal para o gerente
        stageManager.setPrimaryStage(stage);

        // Carrega a primeira tela através do gerente
        stageManager.switchScene("/main.fxml", "Sistema de Doação - Principal");
    }
}