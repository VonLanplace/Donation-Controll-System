package com.vonlanplace.doacao.recebedor;

import com.vonlanplace.doacao.recebedor.auxilio.Auxilio;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCesta;
import com.vonlanplace.doacao.recebedor.dependente.Dependente;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pessoa que recebe as doações.
 *
 * <p><strong>Identificação de negócio é o {@code cpf}, não o {@code id}.</strong> Os
 * índices {@code uk_recebedor_cpf} e {@code uk_recebedor_pix} refletem as duas chaves
 * alternativas usadas pelo serviço para localizar uma pessoa.</p>
 *
 * <p><strong>Senha não existe aqui.</strong> O recebedor é identificado por documento e
 * contato, e não é usuário do sistema.</p>
 *
 * <p>As relações com {@link Residencia}, {@link Dependente} e {@link Auxilio} formam
 * pares bidirecionais. Por isso {@code @Data} é proibido — o {@code toString} gerado
 * recursaria entre recebedor e os filhos até estourar a pilha. A classe usa
 * {@code @ToString} e {@code @EqualsAndHashCode} restritos a campos marcados com
 * {@code Include}.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "recebedor",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_recebedor_cpf", columnNames = "cpf"),
                @UniqueConstraint(name = "uk_recebedor_pix", columnNames = "pix")
        }
)
public class Recebedor {

    /** Identificador do recebedor, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Nome completo da pessoa. Obrigatório. */
    @Column(name = "nome_completo", nullable = false, length = 150)
    @ToString.Include
    private String nomeCompleto;

    /** CPF da pessoa, sem pontos nem traços. Obrigatório e único. */
    @Column(name = "cpf", nullable = false, length = 11)
    private String cpf;

    /** Número do RG. Opcional. */
    @Column(name = "rg_num", length = 20)
    private String rgNum;

    /** Órgão emissor do RG. Opcional, gravado como texto. */
    @Enumerated(EnumType.STRING)
    @Column(name = "rg_emissor", length = 20)
    private OrgaoEmissorRG rgEmissor;

    /** Data de nascimento. Obrigatória. */
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    /** Telefone pessoal, com DDD. Opcional. */
    @Column(name = "telefone_pessoal", length = 15)
    private String telefonePessoal;

    /** Telefone de recado. Opcional. */
    @Column(name = "telefone_recado", length = 15)
    private String telefoneRecado;

    /** Indica se a pessoa sabe ler e escrever. Obrigatório. */
    @Column(name = "le_escreve", nullable = false)
    private boolean leEscreve;

    /** E-mail. Opcional. */
    @Column(name = "email", length = 100)
    private String email;

    /** Chave PIX de recebimento. Opcional, mas única quando informada. */
    @Column(name = "pix", length = 100)
    private String pix;

    /** Indica necessidade de acompanhamento jurídico. Obrigatório. */
    @Column(name = "necessita_juridico", nullable = false)
    private boolean necessitaJuridico;

    /** Indica necessidade de acompanhamento psicológico. Obrigatório. */
    @Column(name = "necessita_psicologo", nullable = false)
    private boolean necessitaPsicologo;

    /** Indica se a entrevista com o entrevistador ocorreu. Obrigatório. */
    @Column(name = "parecer_entrevistador", nullable = false)
    private boolean parecerEntrevistador;

    /** Observações da entrevista. Opcional. */
    @Column(name = "comentarios", length = 1000)
    private String comentarios;

    /** Residência da pessoa. Obrigatória; o lado dono é {@code Residencia.recebedores}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "residencia_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_recebedor_residencia")
    )
    private Residencia residencia;

    /** Dependentes da pessoa. Lado dono: o dependente morre com o recebedor. */
    @OneToMany(mappedBy = "recebedor", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Dependente> dependentes = new ArrayList<>();

    /** Auxílios financeiros recebidos pela pessoa. Lado dono: morrem com o recebedor. */
    @OneToMany(mappedBy = "recebedor", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Auxilio> auxilios = new ArrayList<>();

    /** Histórico de cestas recebidas ou recusadas. Lado dono: morre com o recebedor. */
    @OneToMany(mappedBy = "recebedor", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecebimentoCesta> recebimentos = new ArrayList<>();
}
