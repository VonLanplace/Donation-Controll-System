package edu.fatec.poo.util;

public enum Acesso {
    ADMIN(0),
    USER(1),
    PUBLIC(10);

    private final int indice;

    Acesso(int indice) {
        this.indice = indice;
    }

    public static Acesso getAcesso(int indice) {
        return switch (indice) {
            case 0 -> ADMIN;
            case 1 -> USER;
            case 10 -> PUBLIC;
            default -> throw new IllegalStateException("Unexpected value: " + indice);
        };
    }

    public int getIndice() {
        return indice;
    }
}
