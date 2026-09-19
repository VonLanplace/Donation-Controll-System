package com.vonlanplace.doacao.exception;

public class LoadFailureException extends RuntimeException {

    public LoadFailureException() {
        super("System was not Able to load from Database.");
    }

    public LoadFailureException(Exception e) {
        super("System was not Able to load from Database:\n" + e.getMessage());
    }
    public LoadFailureException(String message) {
        super(message);
    }
}
