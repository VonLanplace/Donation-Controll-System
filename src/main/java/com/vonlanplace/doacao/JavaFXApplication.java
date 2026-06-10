package com.vonlanplace.doacao;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFXApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        // Inicializa o contexto do Spring junto com o JavaFX
        this.context = new SpringApplicationBuilder()
                .sources(DoacaoApplication.class)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Publica um evento do Spring avisando que a tela principal pode abrir
        context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        // Garante que o Spring fecha de forma limpa quando a janela fecha
        this.context.close();
        Platform.exit();
    }
}