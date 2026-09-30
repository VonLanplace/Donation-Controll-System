package com.vonlanplace.doacao.javafx.springIntegration;

import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Reage ao {@link StageReadyEvent} montando a primeira tela.
 *
 * <p><strong>Esta classe é a ponte entre o ciclo de vida do JavaFX e o do Spring.</strong>
 * {@code JavaFXApplication.start} publica o evento, e este listener, registrado como bean
 * porque implementa {@link ApplicationListener}, reage a ele. Como o evento é publicado
 * depois que o contexto terminou de subir, o {@link StageManager} já está pronto para
 * ser usado aqui.</p>
 *
 * <p><strong>Construtor público, ao contrário dos controllers FXML.</strong> Nestas
 * classes quem instancia é o container, e o package-private só evita que o
 * {@code FXMLLoader} tente criar o objeto. Aqui o Spring usa o construtor reflexivo e a
 * visibilidade não importa.</p>
 */
@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    /** Gerenciador de janelas, injetado pelo container. */
    private final StageManager stageManager;

    /**
     * Construtor com injeção de dependências.
     *
     * @param stageManager gerenciador de janelas
     */
    public PrimaryStageInitializer(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    /**
     * Registra a janela principal no gerenciador e carrega a tela inicial.
     *
     * <p>A ordem importa: {@code setPrimaryStage} vem antes de {@code switchScene},
     * porque o {@code switchScene} já escreve título e cena nessa janela — sem o
     * registro, a referência seria {@code null} e a chamada estouraria aqui.</p>
     *
     * @param event evento com a janela principal
     */
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();

        stageManager.setPrimaryStage(stage);

        stageManager.switchScene("/main.fxml", "Sistema de Doação - Principal");
    }
}
