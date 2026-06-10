package com.vonlanplace.doacao;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageManager {

    private final ConfigurableApplicationContext springContext;
    private Stage primaryStage; // Guarda a janela principal

    public StageManager(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    // Define qual é a janela principal (chamado no início)
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Método 1: Substitui o conteúdo da janela principal (Troca de tela)
     */
    public void switchScene(String fxmlPath, String title) {
        try {
            Parent view = loadView(fxmlPath);
            primaryStage.setTitle(title);
            primaryStage.setScene(new Scene(view));
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao mudar de tela: " + fxmlPath, e);
        }
    }

    /**
     * Método 2: Abre uma NOVA janela separada (Pop-up/Modal)
     */
    public void openModal(String fxmlPath, String title) {
        try {
            Parent view = loadView(fxmlPath);
            Stage modalStage = new Stage();
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(view));

            // Bloqueia a janela de trás até que o pop-up seja fechado (opcional)
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(primaryStage);

            modalStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao abrir janela modal: " + fxmlPath, e);
        }
    }

    // Carrega o FXML integrando o Spring à nova tela
    private Parent loadView(String fxmlPath) throws IOException {
        ClassPathResource fxmlResource = new ClassPathResource(fxmlPath);
        FXMLLoader loader = new FXMLLoader(fxmlResource.getURL());
        loader.setControllerFactory(springContext::getBean); // O segredo da integração!
        return loader.load();
    }
}