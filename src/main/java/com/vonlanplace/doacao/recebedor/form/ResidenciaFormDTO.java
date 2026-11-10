package com.vonlanplace.doacao.recebedor.form;

import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Dados residenciais do formulário unificado de recebedor.
 *
 * <p>A residência é sempre criada junto com um recebedor novo e atualizada junto
 * com o recebedor existente. Por isso este DTO não carrega {@code id}: o vínculo
 * é resolvido pelo {@link RecebedorFormService} a partir do recebedor.</p>
 */
@Getter
@Setter
public class ResidenciaFormDTO {

    /** Logradouro da rua. Obrigatório. */
    @NotBlank(message = "O logradouro é obrigatório")
    @Size(max = 150, message = "O logradouro deve ter no máximo 150 caracteres")
    private String logradouro;

    /** Número do endereço. Obrigatório. */
    @NotBlank(message = "O número é obrigatório")
    @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
    private String numero;

    /** CEP sem hífen. Obrigatório. */
    @NotBlank(message = "O CEP é obrigatório")
    @Size(max = 8, message = "O CEP deve ter no máximo 8 caracteres")
    private String cep;

    /** Complemento do endereço. Opcional. */
    @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
    private String complemento;

    /** Cidade. Obrigatória. */
    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    private String cidade;

    /** Bairro. Obrigatório. */
    @NotBlank(message = "O bairro é obrigatório")
    @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
    private String bairro;

    /** Quantidade de moradores. Não negativa. */
    @PositiveOrZero(message = "O número de moradores não pode ser negativo")
    private int numMoradores;

    /** Quantidade de trabalhadores. Não negativa. */
    @PositiveOrZero(message = "O número de trabalhadores não pode ser negativo")
    private int numTrabalhadores;

    /** Perfil de ocupação. Obrigatório. */
    @NotNull(message = "O tipo de ocupação é obrigatório")
    private TipoOcupacao tipoOcupacao;

    /** Tipo de moradia. Obrigatório. */
    @NotNull(message = "O tipo de residência é obrigatório")
    private TipoResidencia tipoResidencia;
}
