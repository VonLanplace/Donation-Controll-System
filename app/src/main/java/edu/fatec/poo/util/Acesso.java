package edu.fatec.poo.util;

import lombok.Getter;

@Getter
public enum Acesso {
    ADMIN(0),
    USER(1000),
    PUBLIC(2000);

    private final int indice;

    Acesso(int indice) {
        this.indice = indice;
    }

    public static Acesso getAcesso(int indice) {
        return switch (indice) {
            case 0 -> ADMIN;
            case 1000 -> USER;
            case 2000 -> PUBLIC;
            default -> throw new IllegalStateException("Unexpected value: " + indice);
        };
    }
}
