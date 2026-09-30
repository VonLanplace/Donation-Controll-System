package com.vonlanplace.doacao.entrevistador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de cadastro de um entrevistador.
 *
 * <p><strong>A senha entra em claro aqui e só existe aqui.</strong> O campo
 * {@code senha} existe porque é o que o formulário envia; a entidade grava
 * {@code senhaHash}, e a conversão para BCrypt acontece no
 * {@link EntrevistadorService}, não no mapper. Nenhum response DTO do projeto
 * tem campo de senha, e este também não.</p>
 *
 * @param nome  nome do entrevistador
 * @param email e-mail de acesso, também é a chave de login
 * @param senha senha em claro, codificada antes de ir para o banco
 */
public record EntrevistadorCreateDTO(
        @NotBlank(message = "O nome do entrevistador é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O e-mail do entrevistador é obrigatório")
        @Email(message = "O e-mail informado não é válido")
        @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        @Size(max = 72, message = "A senha deve ter no máximo 72 caracteres")
        String senha
) {
}
