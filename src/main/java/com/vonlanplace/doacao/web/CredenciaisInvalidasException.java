package com.vonlanplace.doacao.web;

/**
 * E-mail ou senha não conferem com nenhum entrevistador.
 *
 * <p><strong>Uma exceção para os dois casos, de propósito.</strong> Se
 * "e-mail não cadastrado" e "senha errada" fossem mensagens diferentes, o
 * formulário viraria uma ferramenta de descoberta de contas: qualquer um
 * poderia testar endereços e saber quais existem no sistema. A única mensagem
 * possível é a genérica "E-mail ou senha inválidos".</p>
 *
 * <p>Capturada pelo {@link LoginController} e devolvida como erro de
 * formulário, nunca como JSON: é um erro de uso, não do recurso.</p>
 */
public class CredenciaisInvalidasException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem genérica padrão.
     */
    public CredenciaisInvalidasException() {
        super("E-mail ou senha inválidos.");
    }
}