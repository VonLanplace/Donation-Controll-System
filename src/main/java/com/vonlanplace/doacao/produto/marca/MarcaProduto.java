package com.vonlanplace.doacao.produto.marca;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "marca_produto", uniqueConstraints = {
        @UniqueConstraint(name = "uk_marca_produto_nome", columnNames = "nome")
})
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class MarcaProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
}