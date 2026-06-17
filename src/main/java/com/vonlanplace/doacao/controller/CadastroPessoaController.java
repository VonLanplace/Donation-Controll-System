package com.vonlanplace.doacao.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class CadastroPessoaController {

    @FXML
    public void cancelarCadastro(ActionEvent actionEvent) {
        System.out.println("Cadastro Cancelado");
    }

    @FXML
    public void salvarCadastro(ActionEvent actionEvent) {
        System.out.println("Cadastro Salvo");
    }
}
