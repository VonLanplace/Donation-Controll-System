package com.vonlanplace.doacao.support.assertions;

import org.assertj.core.api.AbstractAssert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.Objects;

/**
 * Asserções específicas para respostas RFC 9457.
 */
public final class ProblemDetailAssert
        extends AbstractAssert<ProblemDetailAssert, ProblemDetail> {

    private ProblemDetailAssert(ProblemDetail actual) {
        super(actual, ProblemDetailAssert.class);
    }

    public static ProblemDetailAssert assertThat(ProblemDetail actual) {
        return new ProblemDetailAssert(actual);
    }

    public ProblemDetailAssert hasStatus(HttpStatus esperado) {
        isNotNull();
        if (actual.getStatus() != esperado.value()) {
            failWithMessage("status esperado <%s> mas era <%s>", esperado.value(), actual.getStatus());
        }
        return this;
    }

    public ProblemDetailAssert hasTitle(String esperado) {
        isNotNull();
        if (!Objects.equals(esperado, actual.getTitle())) {
            failWithMessage("title esperado <%s> mas era <%s>", esperado, actual.getTitle());
        }
        return this;
    }

    public ProblemDetailAssert hasDetail(String esperado) {
        isNotNull();
        if (!Objects.equals(esperado, actual.getDetail())) {
            failWithMessage("detail esperado <%s> mas era <%s>", esperado, actual.getDetail());
        }
        return this;
    }

    public ProblemDetailAssert hasTimestamp() {
        isNotNull();
        if (actual.getProperties() == null || actual.getProperties().get("timestamp") == null) {
            failWithMessage("timestamp ausente no ProblemDetail");
        }
        return this;
    }
}
