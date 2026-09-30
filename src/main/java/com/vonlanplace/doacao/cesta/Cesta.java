package com.vonlanplace.doacao.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entrega de uma cesta de produtos a um {@link Recebedor}.
 *
 * <p><strong>Entidade mais simples do projeto.</strong> Sem coleção e sem chave
 * composta: apenas id, data de entrega e uma referência opcional ao
 * recebedor. Não há uma entidade de item de cesta aqui — o que a cesta contém é lido a
 * partir das doações, e não gravado junto dela.</p>
 *
 * <p><strong>Data e recebedor são opcionais.</strong> A coluna {@code data_entrega} não
 * tem {@code nullable = false} e a FK {@code recebedor_id} aceita {@code null}, então
 * existe cesta ainda não entregue e cesta ainda não vinculada a ninguém. Os DTOs não têm
 * {@code @NotNull} por isso.</p>
 *
 * <p>{@code @Data} não causaria recursão aqui, porque não há coleção, mas a classe
 * segue o mesmo padrão do resto do projeto por consistência.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "cesta")
public class Cesta {

    /** Identificador da cesta, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Data da entrega. Opcional: vazia enquanto a cesta não foi entregue. */
    @Column(name = "data_entrega")
    @ToString.Include
    private LocalDate dataEntrega;

    /** Recebedor que recebeu a cesta. Opcional. FK nomeada
     *  {@code fk_cesta_recebedor}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recebedor_id",
            foreignKey = @ForeignKey(name = "fk_cesta_recebedor")
    )
    private Recebedor recebedor;
}
