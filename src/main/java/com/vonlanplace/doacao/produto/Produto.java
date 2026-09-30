package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.cesta.Cesta;
import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Item doável doado por terceiros ou pela própria instituição.
 *
 * <p>Marca e tipo são resolvidos por {@code nome} na camada de serviço, nunca por
 * {@code id}, e são carregados sob demanda ({@code FetchType.LAZY}).</p>
 *
 * <p><strong>Atenção:</strong> {@link #cesta} não possui nenhum caminho de escrita
 * em código — nenhum DTO, serviço ou controller a define. O campo existe no schema
 * e na entidade, mas permanece sempre nulo.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "produto",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_produto_codigo_barras",
                columnNames = "codigo_barras"
        )
)
public class Produto {

    /** Identificador do produto, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Código de barras EAN-13 do produto, único entre todos os produtos. */
    @Column(name = "codigo_barras", length = 13)
    @ToString.Include
    private String codigoBarras;

    /** Marca do produto. Obrigatória. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "marca_produto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_produto_marca_produto")
    )
    private MarcaProduto marcaProduto;

    /** Cesta a que o produto pertence. Opcional e nunca preenchida pelo código atual. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cesta_id",
            foreignKey = @ForeignKey(name = "fk_produto_cesta")
    )
    private Cesta cesta;

    /** Tipo do produto. Obrigatório. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tipo_produto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_produto_tipo_produto")
    )
    private TipoProduto tipoProduto;
}
