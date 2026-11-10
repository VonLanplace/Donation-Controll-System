package com.vonlanplace.doacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Fábrica das respostas {@code application/problem+json}.
 *
 * <p><strong>Existe para as três respostas terem a mesma cara.</strong>
 * {@link GlobalExceptionHandler} monta tudo por aqui, o que garante que
 * {@code type}, {@code title}, {@code status}, {@code detail} e {@code timestamp}
 * apareçam em toda falha de domínio, sem duplicar a montagem em cada
 * {@code @ExceptionHandler}.</p>
 *
 * <p><strong>{@code ProblemDetail.forStatus} preenche {@code type} com o
 * {@code about:blank} padrão</strong> e o {@code status} com o código informado. Só
 * {@code title}, {@code detail} e {@code timestamp} são sets manuais. Como a API não
 * documenta tipos de problema na OpenAPI, o {@code type} não aponta para nada
 * consultável — o cliente precisa ler {@code status}.</p>
 *
 * <p><strong>{@code timestamp} não faz parte do padrão.</strong> É uma propriedade
 * customizada, serializada junto do corpo por {@code setProperty}. É o mesmo para todas
 * as respostas e só serve para correlacionar log de servidor com resposta do cliente.</p>
 */
@Component
public class GenericProblemDetailFactory {

    /**
     * Monta um detalhe de problema com o status informado.
     *
     * @param status status HTTP, que também preenche o campo {@code status} do corpo
     * @param title  título curto e estável do problema
     * @param detail mensagem explicativa; deve ser segura para expor ao cliente
     * @return detalhe do problema pronto para ser serializado
     */
    public ProblemDetail create(
            HttpStatus status,
            String title,
            String detail
    ) {
        ProblemDetail problem =
                ProblemDetail.forStatus(status);

        problem.setTitle(title);
        problem.setDetail(detail);
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }
}
