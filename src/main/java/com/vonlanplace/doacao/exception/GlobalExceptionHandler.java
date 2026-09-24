package com.vonlanplace.doacao.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler
        extends ResponseEntityExceptionHandler {

    private final GenericProblemDetailFactory genericProblemDetailFactory;

    public GlobalExceptionHandler(
            GenericProblemDetailFactory genericProblemDetailFactory
    ) {
        this.genericProblemDetailFactory = genericProblemDetailFactory;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(
            EntityNotFoundException exception
    ) {
        return genericProblemDetailFactory.create(
                HttpStatus.NOT_FOUND,
                "Produto Não Encontrado",
                exception.getMessage()
        );
    }
}
