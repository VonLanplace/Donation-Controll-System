package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.cesta.Cesta;
import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_produto_id")
    private MarcaProduto marcaProduto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cesta_id")
    private Cesta cesta;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_produto_id")
    private TipoProduto tipoProduto;
}