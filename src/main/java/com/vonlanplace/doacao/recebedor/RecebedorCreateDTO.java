package com.vonlanplace.doacao.recebedor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Dados de entrada para o cadastro de um {@link Recebedor}.
 *
 * <p>A residência vem pelo {@code residenciaId} porque o lado dono da relação é
 * {@code Residencia.recebedores}; o serviço cria o recebedor e o liga à residência
 * existente.</p>
 *
 * @param nomeCompleto     nome completo, obrigatório, no máximo 150 caracteres
 * @param cpf              CPF sem pontuação, obrigatório, no máximo 11 caracteres
 * @param rgNum            número do RG, opcional
 * @param rgEmissor        órgão emissor do RG, opcional
 * @param dataNascimento   data de nascimento, obrigatória e no passado
 * @param telefonePessoal  telefone com DDD, opcional
 * @param telefoneRecado   telefone de recado, opcional
 * @param leEscreve        se a pessoa sabe ler e escrever
 * @param email            e-mail válido, opcional
 * @param pix              chave PIX, opcional
 * @param necessitaJuridico se precisa de acompanhamento jurídico
 * @param necessitaPsicologo se precisa de acompanhamento psicológico
 * @param comentarios      observações da entrevista, opcional
 * @param residenciaId     identificador da residência, obrigatório
 */
public record RecebedorCreateDTO(
        @NotBlank(message = "O nome completo é obrigatório")
        @Size(max = 150, message = "O nome completo deve ter no máximo 150 caracteres")
        String nomeCompleto,
        @NotBlank(message = "O CPF é obrigatório")
        @Size(max = 11, message = "O CPF deve ter no máximo 11 caracteres")
        String cpf,
        @Size(max = 20, message = "O número do RG deve ter no máximo 20 caracteres")
        String rgNum,
        OrgaoEmissorRG rgEmissor,
        @NotNull(message = "A data de nascimento é obrigatória")
        @Past(message = "A data de nascimento deve ser no passado")
        LocalDate dataNascimento,
        @Size(max = 15, message = "O telefone pessoal deve ter no máximo 15 caracteres")
        String telefonePessoal,
        @Size(max = 15, message = "O telefone de recado deve ter no máximo 15 caracteres")
        String telefoneRecado,
        boolean leEscreve,
        @Email(message = "O e-mail informado é inválido")
        @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
        String email,
        @Size(max = 100, message = "A chave PIX deve ter no máximo 100 caracteres")
        String pix,
        boolean necessitaJuridico,
        boolean necessitaPsicologo,
        @Size(max = 1000, message = "Os comentários devem ter no máximo 1000 caracteres")
        String comentarios,
        @NotNull(message = "A residência é obrigatória")
        UUID residenciaId
) {
}
