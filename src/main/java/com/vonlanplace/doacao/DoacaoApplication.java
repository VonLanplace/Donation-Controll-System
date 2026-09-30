package com.vonlanplace.doacao;

import com.vonlanplace.doacao.javafx.springIntegration.JavaFXApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do projeto de controle de doações.
 *
 * <p><strong>{@code main} não sobe o servidor web.</strong> A chamada é
 * {@code Application.launch(JavaFXApplication.class)}, e é o
 * {@code JavaFXApplication.init()} que constrói o contexto Spring via
 * {@code SpringApplicationBuilder} e inicia o Tomcat em {@code :8080}. Rodar
 * {@code main} direto exige display X11; sem {@code DISPLAY} o launch falha antes de
 * qualquer requisição ser atendida.</p>
 *
 * <p>Esta classe também é a fonte de Component Scanning: sem {@code scanBasePackages},
 * o Spring usa o pacote desta classe, {@code com.vonlanplace.doacao}, como raiz, o que
 * alcança todos os subpacotes de domínio.</p>
 */
@SpringBootApplication
public class DoacaoApplication {

    /**
     * Inicia a aplicação JavaFX, que por sua vez sobe o contexto Spring e o servidor
     * web.
     *
     * @param args argumentos repassados ao JavaFX
     */
    public static void main(String[] args) {
        Application.launch(JavaFXApplication.class, args);
    }
}
