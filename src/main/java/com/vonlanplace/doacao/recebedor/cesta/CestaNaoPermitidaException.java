package com.vonlanplace.doacao.recebedor.cesta;

/**
 * Recusa de negócio para um recebimento de cesta.
 *
 * <p>Estende {@link IllegalStateException} de propósito: o
 * {@code GlobalExceptionHandler} já traduz essa família para HTTP 409 na API. A tela
 * web captura a exceção e a transforma em erro de campo no formulário.</p>
 */
public class CestaNaoPermitidaException extends IllegalStateException {

    /**
     * Cria a exceção com a mensagem exibida ao usuário.
     *
     * @param message motivo da recusa
     */
    public CestaNaoPermitidaException(String message) {
        super(message);
    }
}
