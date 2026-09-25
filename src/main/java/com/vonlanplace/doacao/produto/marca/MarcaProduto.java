package com.vonlanplace.doacao.produto.marca;

import com.vonlanplace.doacao.produto.Produto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity

@Data
@Table(name = "marca_produto")

public class MarcaProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", unique = true, nullable = false, length = 100)
    private String nome;

    @OneToMany(mappedBy = "marca_produto", fetch = FetchType.LAZY)
    private List<Produto> pessoas = new ArrayList<>();
}