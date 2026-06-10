package com.vonlanplace.doacao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "produto")
@Data
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "codigo_barras", length = 13)
    private String codigoBarras;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "marca_produto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_produto_marca_produto")
    )
    private MarcaProduto marcaProduto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cesta_id",
            foreignKey = @ForeignKey(name = "fk_produto_cesta")
    )
    private Cesta cesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tipo_produto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_produto_tipo_produto")
    )
    private TipoProduto tipoProduto;
}