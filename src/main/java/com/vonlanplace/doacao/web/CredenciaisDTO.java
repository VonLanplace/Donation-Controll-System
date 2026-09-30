package com.vonlanplace.doacao.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credenciais enviadas pelo formulário de login.
 *
 * <p><strong>O e-mail é a chave de acesso.</strong> É o mesmo campo que
 * {@code uk_entrevistador_email} garante único e que {@code findByEmail}
 * consulta no login. A senha chega aqui em claro porque é o que o formulário
 * envia; quem compara com o hash é o {@link AutenticacaoService}, não este
 * record.</p>
 *
 * <p>Validado com {@code @Valid}: campos em branco nem chegam ao serviço.</p>
 *
 * @param email e-mail de acesso
 * @param senha senha em claro, comparada contra o hash BCrypt
 */
public record CredenciaisDTO(
        @NotBlank(message = "Informe o e-mail")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Informe a senha")
        String senha
) {
}