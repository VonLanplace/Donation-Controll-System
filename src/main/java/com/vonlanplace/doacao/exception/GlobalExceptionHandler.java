package com.vonlanplace.doacao.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Tradução central de exceção em resposta HTTP.
 *
 * <p><strong>As respostas usam RFC 9457 {@code application/problem+json}.</strong> Todas
 * as três falhas de domínio devolvem o mesmo formato, montado por
 * {@link GenericProblemDetailFactory}: {@code type}, {@code title}, {@code status},
 * {@code detail} e a propriedade extra {@code timestamp}. O cliente pode tratar o erro
 * pelo {@code status} e ler {@code detail} para a mensagem.</p>
 *
 * <p><strong>A distinção que importa é 404 contra 409.</strong></p>
 * <ul>
 *   <li>{@link EntityNotFoundException} → 404. Todo serviço a usa para "não achei o
 *       registro pedido", inclusive quando o registro existe mas um vínculo informado no
 *       corpo não existe.</li>
 *   <li>{@link IllegalStateException} → 409. É o canal de conflito de regra de negócio:
 *       nome duplicado em marca e tipo, CPF ou PIX repetidos, produto repetido na
 *       doação, exclusão com vínculo pendente. A checagem é feita no serviço justamente
 *       para que a mensagem chegue ao cliente em vez de virar violação de constraint.</li>
 *   <li>{@link DataIntegrityViolationException} → 409 com mensagem genérica fixa. É a
 *       rede de segurança para constraint que o serviço não verificou — por exemplo a
 *       exclusão de uma residência com moradores, que não tem guarda própria. O
 *       {@code detail} não carrega a mensagem do banco de propósito: ela costuma expor
 *       nomes de tabela e coluna.</li>
 * </ul>
 *
 * <p><strong>Estende {@link ResponseEntityExceptionHandler}.</strong> Isso faz o Spring
 * tratar aqui as exceções padrão do MVC, entre elas
 * {@code MethodArgumentNotValidException}, que é o que devolve 400 quando um
 * {@code @Valid} falha num controller — daí o {@code @Valid} nos corpos das requisições
 * produzirem resposta, e não exceção solta.</p>
 *
 * <p><strong>Nada de autenticação.</strong> A aplicação não tem Spring Security
 * configurado, apesar de o bean {@code PasswordEncoder} existir: qualquer chamada às
 * rotas da API é aceita, sem token e sem verificação de papel.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler
        extends ResponseEntityExceptionHandler {

    private final GenericProblemDetailFactory genericProblemDetailFactory;

    /**
     * Construtor com injeção de dependências.
     *
     * @param genericProblemDetailFactory fábrica de respostas problem+json
     */
    public GlobalExceptionHandler(
            GenericProblemDetailFactory genericProblemDetailFactory
    ) {
        this.genericProblemDetailFactory = genericProblemDetailFactory;
    }

    /**
     * Traduz registro não encontrado em 404.
     *
     * @param exception exceção original
     * @return detalhe do problema com o status 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(
            EntityNotFoundException exception
    ) {
        return genericProblemDetailFactory.create(
                HttpStatus.NOT_FOUND,
                "Recurso Não Encontrado",
                exception.getMessage()
        );
    }

    /**
     * Traduz conflito de regra de negócio em 409.
     *
     * @param exception exceção original
     * @return detalhe do problema com o status 409
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(
            IllegalStateException exception
    ) {
        return genericProblemDetailFactory.create(
                HttpStatus.CONFLICT,
                "Conflito de Estado",
                exception.getMessage()
        );
    }

    /**
     * Traduz violação de constraint em 409, com mensagem fixa.
     *
     * @param exception exceção original; a mensagem não é repassada ao cliente
     * @return detalhe do problema com o status 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(
            DataIntegrityViolationException exception
    ) {
        return genericProblemDetailFactory.create(
                HttpStatus.CONFLICT,
                "Conflito de Dados",
                "Operação viola uma restrição de unicidade ou integridade"
        );
    }
}
