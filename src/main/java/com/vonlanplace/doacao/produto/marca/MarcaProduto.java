package com.vonlanplace.doacao.produto.marca;

import com.vonlanplace.doacao.produto.Produto;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Marca comercial de um {@link Produto}.
 *
 * <p><strong>Chave de negócio é o {@code nome}, não o {@code id}.</strong> Todos os
 * métodos deste pacote — serviço, controller e DTO — recebem e devolvem o nome, e nenhum
 * DTO expõe {@code id}. O índice único {@code uk_marca_produto_nome} existe para reforçar
 * isso no banco.</p>
 *
 * <p>A relação com {@link Produto} é bidirecional e usa {@code mappedBy}; o lado
 * inverso é {@code Produto.marcaProduto}. Como é um par bidirecional, {@code @Data} é
 * proibido aqui: o {@code toString} gerado recursaria entre as duas entidades. Por isso
 * a classe usa {@code @ToString} e {@code @EqualsAndHashCode} restritos a campos
 * marcados com {@code Include}.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "marca_produto",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_marca_produto_nome",
                columnNames = "nome"
        )
)
public class MarcaProduto {

    /** Identificador da marca, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Nome da marca. Único e obrigatório. */
    @Column(name = "nome", nullable = false, length = 100)
    @ToString.Include
    private String nome;

    /** Produtos desta marca, lado inverso da relação. Carregado sob demanda. */
    @OneToMany(mappedBy = "marcaProduto", fetch = FetchType.LAZY)
    private List<Produto> produtos = new ArrayList<>();
}
