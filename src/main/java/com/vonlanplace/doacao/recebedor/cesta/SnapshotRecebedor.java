package com.vonlanplace.doacao.recebedor.cesta;

import com.vonlanplace.doacao.recebedor.OrgaoEmissorRG;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Cópia imutável dos dados do recebedor no momento do recebimento.
 *
 * <p><strong>Por que duplicar os dados.</strong> O recibo precisa refletir o cadastro
 * de quando a cesta foi entregue. Se o endereço ou o telefone mudar depois, o recibo
 * antigo não pode ser reescrito. O snapshot é gravado na criação do
 * {@link RecebimentoCesta} e não acompanha edições posteriores do {@code Recebedor}.</p>
 *
 * <p><strong>Colunas prefixadas.</strong> Todas as colunas levam {@code snapshot_} para
 * deixar explícito no banco que aquele dado é histórico, e não o cadastro vivo.</p>
 */
@Getter
@Setter
@Embeddable
public class SnapshotRecebedor {

    /** Nome completo no momento do recebimento. */
    @Column(name = "snapshot_nome_completo", length = 150)
    private String nomeCompleto;

    /** CPF no momento do recebimento. */
    @Column(name = "snapshot_cpf", length = 11)
    private String cpf;

    /** Número do RG no momento do recebimento. */
    @Column(name = "snapshot_rg_num", length = 20)
    private String rgNum;

    /** Órgão emissor do RG no momento do recebimento. */
    @Enumerated(EnumType.STRING)
    @Column(name = "snapshot_rg_emissor", length = 20)
    private OrgaoEmissorRG rgEmissor;

    /** Data de nascimento no momento do recebimento. */
    @Column(name = "snapshot_data_nascimento")
    private LocalDate dataNascimento;

    /** Telefone pessoal no momento do recebimento. */
    @Column(name = "snapshot_telefone_pessoal", length = 15)
    private String telefonePessoal;

    /** Telefone de recado no momento do recebimento. */
    @Column(name = "snapshot_telefone_recado", length = 15)
    private String telefoneRecado;

    /** Se a pessoa sabia ler e escrever no momento do recebimento. */
    @Column(name = "snapshot_le_escreve")
    private boolean leEscreve;

    /** E-mail no momento do recebimento. */
    @Column(name = "snapshot_email", length = 100)
    private String email;

    /** Chave PIX no momento do recebimento. */
    @Column(name = "snapshot_pix", length = 100)
    private String pix;

    /** Se precisava de acompanhamento jurídico no momento do recebimento. */
    @Column(name = "snapshot_necessita_juridico")
    private boolean necessitaJuridico;

    /** Se precisava de acompanhamento psicológico no momento do recebimento. */
    @Column(name = "snapshot_necessita_psicologo")
    private boolean necessitaPsicologo;

    /** Parecer do entrevistador no momento do recebimento. */
    @Column(name = "snapshot_parecer_entrevistador")
    private boolean parecerEntrevistador;

    /** Comentários da entrevista no momento do recebimento. */
    @Column(name = "snapshot_comentarios", length = 1000)
    private String comentarios;

    /** Logradouro no momento do recebimento. */
    @Column(name = "snapshot_logradouro", length = 150)
    private String logradouro;

    /** Número do endereço no momento do recebimento. */
    @Column(name = "snapshot_numero", length = 20)
    private String numero;

    /** Complemento do endereço no momento do recebimento. */
    @Column(name = "snapshot_complemento", length = 100)
    private String complemento;

    /** Bairro no momento do recebimento. */
    @Column(name = "snapshot_bairro", length = 100)
    private String bairro;

    /** Cidade no momento do recebimento. */
    @Column(name = "snapshot_cidade", length = 100)
    private String cidade;

    /** CEP no momento do recebimento. */
    @Column(name = "snapshot_cep", length = 8)
    private String cep;
}
