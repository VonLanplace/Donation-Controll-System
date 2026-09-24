package com.vonlanplace.doacao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class GenericProblemDetailFactory {
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
