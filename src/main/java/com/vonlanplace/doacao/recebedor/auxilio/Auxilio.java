package com.vonlanplace.doacao.recebedor.auxilio;

import com.vonlanplace.doacao.recebedor.Recebedor;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Auxílio financeiro recebido por um {@link Recebedor}.
 *
 * <p>Registro de renda assistencial: um valor que entra na casa por mês e a
 * fonte (Brasília, de um programa, de uma ONG). Junto com as despesas da
 * residência, é a somatória que o entrevistador usa para validar se a família
 * pode receber uma cesta.</p>
 *
 * <p><strong>Não é a necessidade de acompanhamento.</strong> Os campos
 * {@code necessitaJuridico} e {@code necessitaPsicologo} do recebedor marcam
 * encaminhamento social; o auxílio aqui é dinheiro que a família recebe, e a
 * única relação é financeira.</p>
 *
 * <p><strong>Lado filho puro.</strong> O auxílio morre com o recebedor: a
 * relação com {@code CascadeType.ALL} e {@code orphanRemoval} vive no lado
 * dono {@code Recebedor.auxilios}, e este lado só referencia o pai.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "auxilio")
public class Auxilio {

    /** Identificador do auxílio, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Valor mensal do auxílio em reais. Obrigatório. */
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    /** Origem do auxílio, como "Bolsa Família" ou "ONG". Obrigatório. */
    @Column(name = "fonte", nullable = false, length = 100)
    @ToString.Include
    private String fonte;

    /** Detalhe livre do auxílio. Opcional. */
    @Column(name = "descricao", length = 500)
    private String descricao;

    /** Recebedor que recebe o auxílio. O lado dono é {@code Recebedor.auxilios}. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "recebedor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_auxilio_recebedor")
    )
    private Recebedor recebedor;
}