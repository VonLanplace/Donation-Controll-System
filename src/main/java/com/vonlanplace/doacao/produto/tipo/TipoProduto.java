package com.vonlanplace.doacao.produto.tipo;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "tipo_produto")
public class TipoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", unique = true, nullable = false, length = 100)
    private String nome;

    @OneToMany(mappedBy = "tipo_produto", fetch = FetchType.LAZY)
    private List<TipoProduto> pessoas = new ArrayList<>();
}