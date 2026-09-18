package com.vonlanplace.doacao.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException() {
        super("System was not Able to find object.");
    }

    public ObjectNotFoundException(Exception e) {
        super("System was not Able to find object:\n" + e.getMessage());
    }
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
