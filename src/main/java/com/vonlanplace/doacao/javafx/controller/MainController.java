package com.vonlanplace.doacao.javafx.controller;

import com.vonlanplace.doacao.javafx.springIntegration.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

/**
 * Controller FXML da tela principal do JavaFX.
 *
 * <p><strong>É a prova de que o FXML roda sobre beans do Spring.</strong> A classe é
 * anotada com {@code @Component} e recebe o {@link StageManager} por construtor, em vez
 * de ser instanciada pelo {@code FXMLLoader} com {@code new}. Por isso o construtor é
 * package-private: quem cria é o container, não o loader. É o mesmo mecanismo que
 * permite injetar repositórios aqui, como o comentário do exemplo original indica.</p>
 *
 * <p><strong>Os handlers só trocam de cena.</strong> Todos terminam em
 * {@code switchScene} ou em {@code openModal}; nenhum toca em regra de negócio nem
 * consulta o banco. O estado da aplicação vive nos controllers de tela, e a API REST
 * continua sendo o caminho de dados.</p>
 */
@Component
public class MainController {

    /** Gerenciador de janelas, injetado pelo container Spring. */
    private final StageManager stageManager;

    /**
     * Construtor package-private: a instância vem do Spring, não do {@code FXMLLoader}.
     *
     * @param stageManager gerenciador de janelas
     */
    MainController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    /**
     * Treat handler do botão de teste da tela inicial. Imprime no console e abre o
     * cadastro de pessoa.
     */
    @FXML
    public void handleButtonClick() {

        System.out.println("Botão clicado! O Controller é um Bean do Spring.");
        stageManager.switchScene("/CadastroPessoa.fxml", "Sistema de Doação - Principal");
    }

    /**
     * Treat handler do botão de cadastro de doação.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void handleCadastroDoacao(ActionEvent actionEvent) {
        stageManager.switchScene("/CadastroDoacao.fxml", "Sistema de Doação - Principal");
    }

    /**
     * Treat handler do botão de cadastro de visitante.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void handleCadastroVisitante(ActionEvent actionEvent) {
        stageManager.switchScene("/CadastroPessoa.fxml", "Sistema de Doação - Principal");
    }
}
