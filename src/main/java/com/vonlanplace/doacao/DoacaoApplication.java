package com.vonlanplace.doacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do projeto de controle de doações.
 *
 * <p><strong>{@code main} sobe o servidor web primeiro.</strong> O Spring Boot
 * inicializa o contexto e o Tomcat em {@code :8080} sem depender de display. O
 * JavaFX é aberto depois, quando o contexto está pronto, por
 * {@code javafx.springIntegration.JavaFxLauncher} — e só se houver ambiente
 * gráfico. Fechar a janela do JavaFX não derruba a aplicação web.</p>
 *
 * <p><strong>Headless desligado de propósito.</strong> O Spring Boot liga
 * {@code java.awt.headless=true} por padrão, o que faria o JavaFX e o botão de
 * abrir navegador desistirem. {@link SpringApplication#setHeadless(boolean)} é
 * chamado antes do {@code run} para preservar o modo desktop.</p>
 *
 * <p>Esta classe também é a fonte de Component Scanning: sem {@code scanBasePackages},
 * o Spring usa o pacote desta classe, {@code com.vonlanplace.doacao}, como raiz, o que
 * alcança todos os subpacotes de domínio.</p>
 */
@SpringBootApplication
public class DoacaoApplication {

    /**
     * Inicia o contexto Spring e o servidor web.
     *
     * @param args argumentos repassados ao Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DoacaoApplication.class);
        application.setHeadless(false);
        application.run(args);
    }
}
