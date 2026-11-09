package com.vonlanplace.doacao.javafx.springIntegration;

import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.GraphicsEnvironment;
import java.util.Locale;

/**
 * Abre a janela JavaFX depois que a aplicação web está pronta.
 *
 * <p><strong>A web não depende do JavaFX.</strong> O Spring Boot sobe o Tomcat
 * normalmente; este listener reage ao {@link ApplicationReadyEvent} e tenta abrir a
 * janela só quando existe ambiente gráfico. Sem {@code DISPLAY} ou com
 * {@code app.javafx.auto-iniciar=false}, a aplicação segue apenas como servidor web.</p>
 *
 * <p><strong>Fechar a janela não encerra a web.</strong> Nenhum {@code Platform.exit()}
 * é chamado no shutdown do Spring; o Tomcat continua atendendo até o processo ser
 * encerrado.</p>
 */
@Slf4j
@Component
public class JavaFxLauncher {

    /** Gerenciador de janelas usado para montar a tela inicial. */
    private final StageManager stageManager;

    /** Liga/desliga a abertura automática da janela. */
    private final boolean autoIniciar;

    /**
     * Construtor com injeção de dependências.
     *
     * @param stageManager gerenciador de janelas
     * @param autoIniciar  {@code true} abre a janela ao final do boot
     */
    public JavaFxLauncher(
            StageManager stageManager,
            @Value("${app.javafx.auto-iniciar:true}") boolean autoIniciar
    ) {
        this.stageManager = stageManager;
        this.autoIniciar = autoIniciar;
    }

    /**
     * Abre a janela principal quando o contexto está pronto e há display.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void abrirJanela() {
        if (!podeAbrirJanela()) {
            log.info("JavaFX não iniciado: auto-iniciar={} ou ambiente sem display", autoIniciar);
            return;
        }

        try {
            Platform.startup(this::montarJanelaPrincipal);
        } catch (IllegalStateException jaIniciado) {
            log.debug("Toolkit JavaFX já estava iniciado", jaIniciado);
        } catch (Throwable falha) {
            log.warn("Falha ao iniciar o JavaFX; a aplicação web segue normalmente", falha);
        }
    }

    /**
     * Monta a janela principal na thread do JavaFX.
     */
    private void montarJanelaPrincipal() {
        Stage stage = new Stage();
        stageManager.setPrimaryStage(stage);
        stageManager.switchScene("/main.fxml", "Sistema de Doação - Principal");
    }

    /**
     * Diz se o ambiente permite abrir uma janela JavaFX.
     *
     * @return {@code true} quando a abertura automática está ligada e há display
     */
    private boolean podeAbrirJanela() {
        if (!autoIniciar || GraphicsEnvironment.isHeadless()) {
            return false;
        }
        String sistema = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (sistema.contains("linux")) {
            String display = System.getenv("DISPLAY");
            return display != null && !display.isBlank();
        }
        return true;
    }
}
