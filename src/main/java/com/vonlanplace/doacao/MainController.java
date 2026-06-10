package com.vonlanplace.doacao;

import org.springframework.stereotype.Component;
import javafx.fxml.FXML;

@Component // O Spring agora gerencia essa classe!
public class MainController {

    // Exemplo: Você pode injetar seus Repositories do H2 aqui normalmente!
    // @Autowired
    // private DoacaoRepository repository;

    @FXML
    public void handleButtonClick() {
        System.out.println("Botão clicado! O Controller é um Bean do Spring.");
    }
}