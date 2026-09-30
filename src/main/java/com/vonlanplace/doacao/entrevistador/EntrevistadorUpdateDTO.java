package com.vonlanplace.doacao.entrevistador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados de edição de um entrevistador.
 *
 * <p><strong>A senha é opcional, e a opcionalidade significa "não mexer".</strong>
 * Deixar o campo em branco no formulário de edição mantém o hash que já está
 * no banco, porque o service só chama o encoder quando recebe valor. Isso é o
 * que impede que editar o nome de alguém apague a senha da pessoa.</p>
 *
 * <p>{@code null} e string vazia são a mesma coisa aqui: o binding de um input
 * não preenchido chega como {@code null} ou {@code ""} conforme o navegador, e
 * nenhum dos dois pode ser lido como "senha nova é vazia".</p>
 *
 * @param nome  nome do entrevistador
 * @param email e-mail de acesso
 * @param senha nova senha, ou vazio para manter a atual
 */
public record EntrevistadorUpdateDTO(
        @NotBlank(message = "O nome do entrevistador é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O e-mail do entrevistador é obrigatório")
        @Email(message = "O e-mail informado não é válido")
        @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
        String email,

        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        @Size(max = 72, message = "A senha deve ter no máximo 72 caracteres")
        String senha
) {
}
