package edu.fatec.poo.exceptions.invalido;

public class EmailInvalidoException extends IllegalArgumentException {
    public EmailInvalidoException(String message) {
        super(message);
    }
}
