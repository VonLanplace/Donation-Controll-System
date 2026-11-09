package com.vonlanplace.doacao.recebedor.form;

import com.vonlanplace.doacao.recebedor.OrgaoEmissorRG;
import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulário único de cadastro e edição de {@link Recebedor}.
 *
 * <p>Reúne os dados pessoais, a residência, os dependentes, os auxílios e as
 * despesas do recebedor. A persistência é orquestrada pelo
 * {@link RecebedorFormService}, que cria a residência junto com um recebedor novo
 * e sincroniza os filhos na edição.</p>
 */
@Getter
@Setter
public class RecebedorFormDTO {

    /** Nome completo. Obrigatório. */
    @NotBlank(message = "O nome completo é obrigatório")
    @Size(max = 150, message = "O nome completo deve ter no máximo 150 caracteres")
    private String nomeCompleto;

    /** CPF sem pontuação. Obrigatório. */
    @NotBlank(message = "O CPF é obrigatório")
    @Size(max = 11, message = "O CPF deve ter no máximo 11 caracteres")
    private String cpf;

    /** Número do RG. Opcional. */
    @Size(max = 20, message = "O número do RG deve ter no máximo 20 caracteres")
    private String rgNum;

    /** Órgão emissor do RG. Opcional. */
    private OrgaoEmissorRG rgEmissor;

    /** Data de nascimento. Obrigatória e no passado. */
    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser no passado")
    private LocalDate dataNascimento;

    /** Telefone pessoal. Opcional. */
    @Size(max = 15, message = "O telefone pessoal deve ter no máximo 15 caracteres")
    private String telefonePessoal;

    /** Telefone de recado. Opcional. */
    @Size(max = 15, message = "O telefone de recado deve ter no máximo 15 caracteres")
    private String telefoneRecado;

    /** Indica se a pessoa sabe ler e escrever. */
    private boolean leEscreve;

    /** E-mail. Opcional. */
    @Email(message = "O e-mail informado é inválido")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres")
    private String email;

    /** Chave PIX. Opcional. */
    @Size(max = 100, message = "A chave PIX deve ter no máximo 100 caracteres")
    private String pix;

    /** Indica necessidade de acompanhamento jurídico. */
    private boolean necessitaJuridico;

    /** Indica necessidade de acompanhamento psicológico. */
    private boolean necessitaPsicologo;

    /** Status da cesta no mês corrente. Opcional. */
    private StatusRecebimento statusCesta;

    /** Indica se a entrevista com o entrevistador ocorreu. */
    private boolean parecerEntrevistador;

    /** Observações da entrevista. Opcional. */
    @Size(max = 1000, message = "Os comentários devem ter no máximo 1000 caracteres")
    private String comentarios;

    /** Dados residenciais. Obrigatório. */
    @Valid
    @NotNull(message = "Os dados da residência são obrigatórios")
    private ResidenciaFormDTO residencia = new ResidenciaFormDTO();

    /** Dependentes do recebedor. */
    @Valid
    private List<DependenteFormDTO> dependentes = new ArrayList<>();

    /** Auxílios financeiros do recebedor. */
    @Valid
    private List<AuxilioFormDTO> auxilios = new ArrayList<>();

    /** Despesas da residência do recebedor. */
    @Valid
    private List<DespesaFormDTO> despesas = new ArrayList<>();
}
