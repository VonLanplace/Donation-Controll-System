package com.vonlanplace.doacao.recebedor.form;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Linha de dependente do formulário unificado de recebedor.
 *
 * <p>O {@code id} é {@code null} para uma linha nova e preenchido para uma linha
 * já persistida. A validação é feita pelo {@link RecebedorFormValidator}, que
 * ignora linhas completamente vazias.</p>
 */
@Getter
@Setter
public class DependenteFormDTO {

    /** Identificador do dependente, ou {@code null} numa linha nova. */
    private UUID id;

    /** Nome do dependente. Obrigatório quando a linha está preenchida. */
    private String nome;

    /** Idade informada na entrevista. */
    private int idade;

    /** Data de nascimento, quando conhecida. */
    private LocalDate dataNascimento;
}
