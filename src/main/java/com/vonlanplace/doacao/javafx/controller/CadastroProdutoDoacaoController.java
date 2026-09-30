package com.vonlanplace.doacao.javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

/**
 * Controller FXML da janela de cadastro de produto doado.
 *
 * <p><strong>Os dois handlers são vazios de verdade.</strong> Sem corpo, sem
 * {@code println}, sem retorno. A janela existe e os botões existem, mas confirmar e
 * cancelar não fazem nada — a tela é aberta por
 * {@code CadastroDoacaoController.handleAdicionarProduto} e fica parada ali. É o
 * controller mais incompleto do conjunto.</p>
 */
@Component
public class CadastroProdutoDoacaoController {

    /**
     * Treat handler de confirmação. Sem implementação.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void handleConfirmar(ActionEvent actionEvent) {
    }

    /**
     * Treat handler de cancelamento. Sem implementação.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void handleCancelar(ActionEvent actionEvent) {
    }
}
