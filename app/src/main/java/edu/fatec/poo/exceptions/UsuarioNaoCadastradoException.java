package edu.fatec.poo.exceptions;

public class UsuarioNaoCadastradoException extends RuntimeException {
    public UsuarioNaoCadastradoException(String message) {
        super(message);
    }

    public UsuarioNaoCadastradoException() {
        super("Usuário não Cadastrado no Sistema");
    }
}
