package com.vonlanplace.doacao.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Montagem padronizada de respostas RFC 9457.
 */
@Tag("unit")
@DisplayName("GenericProblemDetailFactory")
class GenericProblemDetailFactoryTest {

    private final GenericProblemDetailFactory factory = new GenericProblemDetailFactory();

    @ParameterizedTest(name = "status {0}")
    @EnumSource(value = HttpStatus.class, names = {"BAD_REQUEST", "NOT_FOUND", "CONFLICT"})
    void montaProblemDetail(HttpStatus status) {
        ProblemDetail problem = factory.create(status, "Título", "Detalhe");

        assertThat(problem.getStatus()).isEqualTo(status.value());
        assertThat(problem.getTitle()).isEqualTo("Título");
        assertThat(problem.getDetail()).isEqualTo("Detalhe");
        assertThat(problem.getProperties()).containsKey("timestamp");
        assertThat(problem.getProperties().get("timestamp")).isInstanceOf(Instant.class);
    }
}
