package com.vonlanplace.doacao.produto.tipo;

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
 * Categoria de um {@link Produto}, por exemplo "Roupa" ou "Alimento".
 *
 * <p><strong>Chave de negócio é o {@code nome}, não o {@code id}.</strong> Toda a API
 * deste pacote usa o nome, e nenhum DTO expõe {@code id}. O índice único
 * {@code uk_tipo_produto_nome} garante isso no banco.</p>
 *
 * <p>A relação com {@link Produto} é bidirecional e usa {@code mappedBy}; o lado inverso
 * é {@code Produto.tipoProduto}. Por ser um par bidirecional, {@code @Data} é proibido
 * aqui — o {@code toString} gerado recursaria entre as duas entidades.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "tipo_produto",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_tipo_produto_nome",
                columnNames = "nome"
        )
)
public class TipoProduto {

    /** Identificador do tipo, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Nome do tipo. Único e obrigatório. */
    @Column(name = "nome", nullable = false, length = 100)
    @ToString.Include
    private String nome;

    /** Produtos deste tipo, lado inverso da relação. Carregado sob demanda. */
    @OneToMany(mappedBy = "tipoProduto", fetch = FetchType.LAZY)
    private List<Produto> produtos = new ArrayList<>();
}
