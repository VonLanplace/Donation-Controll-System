package com.vonlanplace.doacao.javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

/**
 * Controller FXML da tela de cadastro de pessoa.
 *
 * <p><strong>Andaime completo, e o mais vazio dos quatro.</strong> Os dois handlers só
 * imprimem no console: não há campo, não há validação, nada é lido do formulário e
 * nada é salvo. Diferente de {@link CadastroDoacaoController}, nem sequer injeta
 * dependência — não precisa de {@code StageManager} porque não troca de cena.</p>
 *
 * <p>Mantém {@code @Component} mesmo sem injeção, para que a tela siga o mesmo caminho
 * de resolução dos demais controllers FXML. Assim que precisar de repositório, o
 * construtor package-private é o ponto de entrada.</p>
 */
@Component
public class CadastroPessoaController {

    /**
     * Treat handler de cancelamento. Ainda não volta para a tela anterior.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void cancelarCadastro(ActionEvent actionEvent) {
        System.out.println("Cadastro Cancelado");
    }

    /**
     * Treat handler de salvamento. Ainda não grava a pessoa.
     *
     * @param actionEvent evento do JavaFX, não utilizado
     */
    @FXML
    public void salvarCadastro(ActionEvent actionEvent) {
        System.out.println("Cadastro Salvo");
    }
}
