package com.vonlanplace.doacao.exception;

public class SaveFailureException extends RuntimeException {

    public SaveFailureException() {
        super("System was not Able to save to Database.");
    }

    public SaveFailureException(Exception e) {
        super("System was not Able to save to Database:\n" + e.getMessage());
    }

    public SaveFailureException(String message) {
        super(message);
    }
}
