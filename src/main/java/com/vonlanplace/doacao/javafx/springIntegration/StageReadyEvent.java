package com.vonlanplace.doacao.javafx.springIntegration;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

/**
 * Evento que avisa que a janela principal já existe e pode receber uma tela.
 *
 * <p><strong>Publicada por {@code JavaFXApplication.start}, consumida por
 * {@link PrimaryStageInitializer}.</strong> Existe para quebrar a dependência circular
 * entre "o JavaFX tem a janela" e "o Spring tem os controllers": a aplicação não pode
 * chamar o gerenciador de telas diretamente, porque no momento do {@code start} o
 * container ainda não terminou de subir. Publicar um evento deixa o container avisar
 * quando estiver pronto.</p>
 *
 * <p><strong>A {@code Stage} viaja como {@code source}, e é isso que
 * {@link #getStage()} faz.</strong> Não há campo próprio para a janela: ela é a origem
 * do {@link ApplicationEvent}, e o {@code getStage()} é só um cast com nome mais
 * claro.</p>
 */
public class StageReadyEvent extends ApplicationEvent {

    /**
     * Cria o evento.
     *
     * @param stage janela principal pronta para receber uma tela
     */
    public StageReadyEvent(Stage stage) {
        super(stage);
    }

    /**
     * Recupera a janela que originou o evento.
     *
     * @return janela principal
     */
    public Stage getStage() {
        return (Stage) getSource();
    }
}
