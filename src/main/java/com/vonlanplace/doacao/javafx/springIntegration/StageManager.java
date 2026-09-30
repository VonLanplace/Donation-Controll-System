package com.vonlanplace.doacao.javafx.springIntegration;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Central de troca de telas e abertura de modais do JavaFX.
 *
 * <p><strong>Guarda a janela principal, não as telas.</strong> Só a {@code primaryStage}
 * é mantida em campo; cada navegação recria a {@code Scene}. Isso significa que estado de
 * tela anterior não sobrevive à troca — os controllers FXML são beans do singletão do
 * Spring e, por isso, mantêm estado entre navegações, mas a árvore visual não.</p>
 *
 * <p><strong>{@link #loadView(String)} é onde a integração acontece.</strong> O
 * {@code FXMLLoader.setControllerFactory(springContext::getBean)} faz o FXML pedir seus
 * controllers ao container em vez de instanciá-los. Por isso os controllers FXML são
 * {@code @Component} com construtor package-private: sem isso, o loader tentaria
 * chamá-los por construtor vazio e a injeção falharia.</p>
 *
 * <p><strong>Construtor público</strong>, ao contrário dos controllers FXML: aqui quem
 * instancia é o Spring, não o {@code FXMLLoader}.</p>
 */
@Component
public class StageManager {

    /** Contexto Spring usado como fábrica de controllers para o {@code FXMLLoader}. */
    private final ConfigurableApplicationContext springContext;

    /** Janela principal, registrada por {@link PrimaryStageInitializer} antes do primeiro uso. */
    private Stage primaryStage;

    /**
     * Construtor com injeção de dependências.
     *
     * @param springContext contexto Spring, usado como fábrica de controllers FXML
     */
    public StageManager(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    /**
     * Define qual é a janela principal. Chamado uma vez, antes de qualquer troca de
     * tela.
     *
     * @param primaryStage janela principal do JavaFX
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Substitui o conteúdo da janela principal.
     *
     * <p>Chamar antes de {@link #setPrimaryStage(Stage)} estoura um
     * {@code NullPointerException} em {@code primaryStage.setTitle}: a referência ainda
     * é {@code null}.</p>
     *
     * @param fxmlPath caminho do FXML no classpath, com barra inicial
     * @param title    título da janela
     * @throws RuntimeException se o FXML não existir ou estiver malformado; a
     *                          {@link IOException} original fica como causa
     */
    public void switchScene(String fxmlPath, String title) throws RuntimeException {
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
     * Abre uma janela nova, separada, sobre a principal.
     *
     * <p>Usa {@link Modality#WINDOW_MODAL}: a janela de trás continua desenhada mas não
     * recebe entrada, e a principal é ligada como dona. Fechar o modal devolve o
     * controle à principal sem que o estado visual dela seja recarregado.</p>
     *
     * @param fxmlPath caminho do FXML no classpath, com barra inicial
     * @param title    título da janela modal
     * @throws RuntimeException se o FXML não existir ou estiver malformado; a
     *                          {@link IOException} original fica como causa
     */
    public void openModal(String fxmlPath, String title) throws RuntimeException {
        try {
            Parent view = loadView(fxmlPath);
            Stage modalStage = new Stage();
            modalStage.setTitle(title);
            modalStage.setScene(new Scene(view));

            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(primaryStage);

            modalStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao abrir janela modal: " + fxmlPath, e);
        }
    }

    /**
     * Carrega um FXML resolvendo os controllers no container Spring.
     *
     * @param fxmlPath caminho do FXML no classpath, com barra inicial
     * @return raiz visual da tela carregada
     * @throws IOException se o recurso não existir ou o FXML não puder ser processado
     */
    private Parent loadView(String fxmlPath) throws IOException {
        ClassPathResource fxmlResource = new ClassPathResource(fxmlPath);
        FXMLLoader loader = new FXMLLoader(fxmlResource.getURL());
        loader.setControllerFactory(springContext::getBean);
        return loader.load();
    }
}
