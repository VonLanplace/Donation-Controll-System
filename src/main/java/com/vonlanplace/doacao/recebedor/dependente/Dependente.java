package com.vonlanplace.doacao.recebedor.dependente;

import com.vonlanplace.doacao.recebedor.Recebedor;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Dependente de um {@link Recebedor}.
 *
 * <p>Um recebedor tem N dependentes, e o dependente não é uma pessoa do
 * sistema — não tem CPF, não recebe cesta nem aparece nas buscas. É um
 * registro da entrevista: nome, idade e, quando conhecida, a data de
 * nascimento. A idade é o dado de rotina do cadastro; a data de nascimento é
 * o registro fino que permite conferir anos depois.</p>
 *
 * <p><strong>Lado filho puro.</strong> O dependente morre com o recebedor: a
 * relação com {@code CascadeType.ALL} e {@code orphanRemoval} vive no lado
 * dono {@code Recebedor.dependentes}, e este lado só referencia o pai.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "dependente")
public class Dependente {

    /** Identificador do dependente, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Nome do dependente. Obrigatório. */
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    /** Idade informada na entrevista. Obrigatória. */
    @Column(name = "idade", nullable = false)
    private int idade;

    /** Data de nascimento, quando conhecida. Opcional. */
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    /** Recebedor a quem o dependente pertence. O lado dono é {@code Recebedor.dependentes}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "recebedor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dependente_recebedor")
    )
    private Recebedor recebedor;
}