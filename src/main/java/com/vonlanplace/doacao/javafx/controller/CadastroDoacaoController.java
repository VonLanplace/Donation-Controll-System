package com.vonlanplace.doacao.javafx.controller;

import com.vonlanplace.doacao.javafx.springIntegration.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

/**
 * Controller FXML da tela de cadastro de doação.
 *
 * <p><strong>Andaime: os quatro handlers de ação ainda não fazem nada.</strong> Todos
 * terminam em {@code System.out.println}. Não há campo na tela ligado a este
 * controller, não há validação e nada é persistido — a tela não cadastra doação
 * nenhuma. A única ação que faz alguma coisa é
 * {@link #handleAdicionarProduto(ActionEvent)}, que abre a janela de item.</p>
 *
 * <p><strong>Segue o mesmo padrão de integração de {@link MainController}:</strong>
 * {@code @Component} com construtor package-private alimentado pelo Spring.</p>
 */
@Component
public class CadastroDoacaoController {

    /** Gerenciador de janelas, injetado pelo container Spring. */
    private final StageManager stageManager;

    /**
     * Construtor package-private: a instância vem do Spring, não do {@code FXMLLoader}.
     *
     * @param stageManager gerenciador de janelas
     */
    CadastroDoacaoController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    /**
     * Treat handler de remoção de item da doação. Ainda não remove nada.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void handleRemoverProduto(ActionEvent actionEvent) {
        System.out.println("Remover Produto");
    }

    /**
     * Treat handler de adição de item. Abre a janela de cadastro de produto em modal.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void handleAdicionarProduto(ActionEvent actionEvent) {
        System.out.println("Adicionar Produto");
        stageManager.openModal("/CadastroProdutoDoacao.fxml", "Sistema de Doação - Principal");
    }

    /**
     * Treat handler de exclusão da doação. Ainda não exclui nada.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void handleDeletar(ActionEvent actionEvent) {
        System.out.println("Deletar Doacao");
    }

    /**
     * Treat handler de cancelamento. Ainda não volta para a tela anterior.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void handleCancelar(ActionEvent actionEvent) {
        System.out.println("Cancelar Doacao");
    }

    /**
     * Treat handler de conclusão do cadastro. Ainda não grava a doação.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void handleFinalizarCadastro(ActionEvent actionEvent) {
        System.out.println("Finalizar Doacao");
    }
}
