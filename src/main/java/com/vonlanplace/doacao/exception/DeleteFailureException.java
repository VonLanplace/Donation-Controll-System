package com.vonlanplace.doacao.exception;

public class DeleteFailureException extends RuntimeException {


    public DeleteFailureException() {
        super("System was not Able to delete from Database.");
    }

    public DeleteFailureException(Exception e) {
        super("System was not Able to delete from Database:\n" + e.getMessage());
    }
    public DeleteFailureException(String message) {
        super(message);
    }
}
