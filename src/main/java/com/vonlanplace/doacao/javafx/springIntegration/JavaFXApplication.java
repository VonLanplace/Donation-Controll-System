package com.vonlanplace.doacao.javafx.springIntegration;

import com.vonlanplace.doacao.DoacaoApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Aplicação JavaFX que também é o bootstrap do Spring Boot.
 *
 * <p><strong>É aqui que o servidor web nasce.</strong> {@code DoacaoApplication.main}
 * não sobe o Tomcat: ele chama {@code Application.launch(JavaFXApplication.class)},
 * e é o {@link #init()} desta classe que constrói o contexto Spring por
 * {@link SpringApplicationBuilder} com {@code DoacaoApplication} como fonte. Ou seja, a
 * API REST em {@code :8080} só responde depois que o JavaFX inicializa. Fechar a janela
 * principal derruba o servidor junto — ver {@link #stop()}.</p>
 *
 * <p><strong>Sem display, nada sobe.</strong> O {@code launch} exige X11; em máquina sem
 * {@code DISPLAY} o processo falha antes de o Tomcat abrir a porta. A instrução
 * "Unsupported JavaFX configuration: classes were loaded from 'unnamed module'" aparece
 * no log e é inofensiva.</p>
 *
 * <p><strong>Por que o evento e não uma chamada direta.</strong> {@link #start(Stage)}
 * não monta a interface: só publica {@link StageReadyEvent}. Quem reage é
 * {@link PrimaryStageInitializer}, um bean do Spring, que chama
 * {@code StageManager.switchScene}. Isso garante que a tela só abre depois que o
 * container está pronto para fornecer os controllers FXML como beans.</p>
 */
public class JavaFXApplication extends Application {

    /** Contexto Spring criado em {@link #init()}, fechado em {@link #stop()}. */
    private ConfigurableApplicationContext context;

    /**
     * Sobe o contexto Spring antes de qualquer tela aparecer.
     *
     * <p>Usa {@code getParameters().getRaw()}, que devolve os argumentos recebidos pelo
     * {@code launch}, e os repassa ao Spring — é por aí que configurações de
     * ambiente, como perfil ou porta, chegam ao boot.</p>
     */
    @Override
    public void init() {
        this.context = new SpringApplicationBuilder()
                .sources(DoacaoApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    /**
     * Publica o evento que dispara a abertura da tela principal.
     *
     * @param primaryStage janela principal fornecida pelo JavaFX
     * @throws Exception se a publicação do evento falhar
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        context.publishEvent(new StageReadyEvent(primaryStage));
    }

    /**
     * Fecha o contexto Spring e encerra a plataforma JavaFX ao fechar a janela.
     *
     * <p>Sem o {@code close()}, as conexões do pool e o Tomcat ficariam abertos até o
     * processo morrer. O {@code Platform.exit()} encerra a thread de aplicação do
     * JavaFX, que por padrão não morre sozinha quando a última janela fecha.</p>
     */
    @Override
    public void stop() {
        this.context.close();
        Platform.exit();
    }
}
