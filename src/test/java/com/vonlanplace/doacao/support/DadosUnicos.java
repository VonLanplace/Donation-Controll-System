package com.vonlanplace.doacao.support;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Gera documentos e chaves únicos para testes que não rodam em transação.
 *
 * <p>Testes anotados com {@code @Transactional} fazem rollback e podem repetir
 * os mesmos valores. Testes de API e de contexto que persistem dados precisam
 * de valores livres, e esta classe evita que a ordem de execução importe.</p>
 */
public final class DadosUnicos {

    private static final AtomicLong SEQUENCIA = new AtomicLong(1);

    private DadosUnicos() {
    }

    /**
     * Gera um CPF numérico de 11 dígitos.
     *
     * @return CPF único
     */
    public static String cpf() {
        return String.format("%011d", SEQUENCIA.getAndIncrement());
    }

    /**
     * Gera um RG único.
     *
     * @return RG único
     */
    public static String rg() {
        return "RG" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Gera uma chave PIX única.
     *
     * @return chave PIX única
     */
    public static String pix() {
        return "pix-" + UUID.randomUUID() + "@teste.local";
    }

    /**
     * Gera um nome único a partir de um prefixo.
     *
     * @param prefixo prefixo do nome
     * @return nome único
     */
    public static String nome(String prefixo) {
        return prefixo + " " + SEQUENCIA.getAndIncrement();
    }
}
