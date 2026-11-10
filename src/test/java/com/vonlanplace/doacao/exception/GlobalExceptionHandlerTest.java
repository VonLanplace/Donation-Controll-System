package com.vonlanplace.doacao.exception;

import com.vonlanplace.doacao.support.assertions.ProblemDetailAssert;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

/**
 * Tradução das exceções de domínio para RFC 9457.
 */
@Tag("unit")
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler(new GenericProblemDetailFactory());

    @Test
    @DisplayName("registro não encontrado vira 404")
    void notFound() {
        ProblemDetail problem = handler.handleEntityNotFoundException(
                new EntityNotFoundException("Recebedor not found"));

        ProblemDetailAssert.assertThat(problem)
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasTitle("Recurso Não Encontrado")
                .hasDetail("Recebedor not found")
                .hasTimestamp();
    }

    @Test
    @DisplayName("argumento inválido vira 400")
    void badRequest() {
        ProblemDetail problem = handler.handleIllegalArgumentException(
                new IllegalArgumentException("Mês inválido"));

        ProblemDetailAssert.assertThat(problem)
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasTitle("Requisição Inválida")
                .hasDetail("Mês inválido")
                .hasTimestamp();
    }

    @Test
    @DisplayName("conflito de estado vira 409")
    void conflict() {
        ProblemDetail problem = handler.handleIllegalStateException(
                new IllegalStateException("CPF já cadastrado"));

        ProblemDetailAssert.assertThat(problem)
                .hasStatus(HttpStatus.CONFLICT)
                .hasTitle("Conflito de Estado")
                .hasDetail("CPF já cadastrado")
                .hasTimestamp();
    }

    @Test
    @DisplayName("violação de integridade vira 409 com mensagem fixa")
    void dataIntegrity() {
        ProblemDetail problem = handler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("constraint uk_recebedor_cpf"));

        ProblemDetailAssert.assertThat(problem)
                .hasStatus(HttpStatus.CONFLICT)
                .hasTitle("Conflito de Dados")
                .hasDetail("Operação viola uma restrição de unicidade ou integridade")
                .hasTimestamp();
    }
}
