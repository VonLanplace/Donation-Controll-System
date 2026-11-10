package com.vonlanplace.doacao.recebedor.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Registro histórico de uma oferta de cesta a um {@link Recebedor}.
 *
 * <p><strong>Um registro por mês.</strong> A constraint
 * {@code uk_recebimento_cesta_recebedor_mes} impede duas linhas para o mesmo
 * recebedor no mesmo mês. A data canônica é {@code dataReferencia}, sempre no
 * primeiro dia do mês, porque {@code @Enumerated(STRING)} ordenaria os meses
 * alfabeticamente.</p>
 *
 * <p><strong>O snapshot é a fonte do recibo.</strong> O recibo em PDF e o CSV usam
 * {@link #snapshot}, nunca o cadastro atual do recebedor.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "recebimento_cesta",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_recebimento_cesta_recebedor_mes",
                        columnNames = {"recebedor_id", "data_referencia"}
                )
        },
        indexes = {
                @Index(name = "idx_recebimento_cesta_data_referencia", columnList = "data_referencia")
        }
)
public class RecebimentoCesta {

    /** Identificador do registro, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Recebedor dono do registro. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "recebedor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_recebimento_cesta_recebedor")
    )
    private Recebedor recebedor;

    /** Primeiro dia do mês de referência. */
    @Column(name = "data_referencia", nullable = false)
    @ToString.Include
    private LocalDate dataReferencia;

    /** Data efetiva da entrega, quando informada. */
    @Column(name = "data_entrega")
    private LocalDate dataEntrega;

    /** Resultado da oferta: recebeu ou recusou. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @ToString.Include
    private StatusRecebimento status;

    /** Cópia dos dados do recebedor no momento do registro. */
    @Embedded
    private SnapshotRecebedor snapshot;

    /** Observação livre sobre a oferta. */
    @Column(name = "observacao", length = 1000)
    private String observacao;
}
