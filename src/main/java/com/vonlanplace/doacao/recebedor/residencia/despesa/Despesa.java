package com.vonlanplace.doacao.recebedor.residencia.despesa;

import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Despesa de uma {@link Residencia}.
 *
 * <p>O registro financeiro que o entrevistador levanta para validar se a
 * família pode receber uma cesta: quantos reais a casa compromete por mês e
 * de onde esse valor vem (fonte). A descrição é o detalhe livre.</p>
 *
 * <p><strong>Lado filho puro.</strong> A despesa morre com a residência: a
 * relação com {@code CascadeType.ALL} e {@code orphanRemoval} vive no lado
 * dono {@code Residencia.despesas}, e este lado só referencia o pai.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "despesa")
public class Despesa {

    /** Identificador da despesa, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Valor mensal da despesa em reais. Obrigatório. */
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /** Origem da despesa, como "aluguel" ou "conta de luz". Obrigatório. */
    @Column(name = "fonte", nullable = false, length = 100)
    @ToString.Include
    private String fonte;

    /** Detalhe livre da despesa. Opcional. */
    @Column(name = "descricao", length = 500)
    private String descricao;

    /** Residência a que a despesa pertence. O lado dono é {@code Residencia.despesas}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "residencia_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_despesa_residencia")
    )
    private Residencia residencia;
}