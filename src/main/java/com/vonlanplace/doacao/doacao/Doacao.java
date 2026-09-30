package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.entrevistador.Entrevistador;
import com.vonlanplace.doacao.recebedor.Recebedor;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Registro de uma doação: o que foi doado, por quem, para quem e em que data.
 *
 * <p><strong>Recebedor e entrevistador são opcionais.</strong> As duas colunas aceitam
 * {@code null}: uma doação ainda não vinculada a um recebedor cadastro continua válida
 * e aparece no registro com o campo aninhado ausente. Já a lista de produtos é
 * obrigatória na prática, e o serviço recusa uma doação sem itens.</p>
 *
 * <p><strong>Esta classe é a raiz do agregado {@code Doacao → DoacaoProduto}.</strong> É
 * o único lugar do projeto com {@code cascade = CascadeType.ALL} e
 * {@code orphanRemoval = true}, justamente para que a troca da lista inteira de produtos
 * na atualização funcione: os itens removidos do corpo são órfãos e some com a
 * persistence context, os novos são inseridos. {@code DoacaoProduto} não tem cascade
 * para o próprio lado.</p>
 *
 * <p>As relações com {@link Recebedor} e {@code DoacaoProduto} são bidirecionais, então
 * {@code @Data} é proibido aqui — o {@code toString} gerado recursaria.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "doacao")
public class Doacao {

    /** Identificador da doação, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Data da doação. Obrigatória. */
    @Column(name = "data", nullable = false)
    @ToString.Include
    private LocalDate data;

    /** Nome de quem doou. Opcional: pode haver recebedor identificado em vez disso. */
    @Column(name = "nome_doador", length = 150)
    @ToString.Include
    private String nomeDoador;

    /** Recebedor contemplado. Opcional. FK nomeada {@code fk_doacao_recebedor}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "recebedor_id",
            foreignKey = @ForeignKey(name = "fk_doacao_recebedor")
    )
    private Recebedor recebedor;

    /** Entrevistador responsável pelo registro. Opcional. FK
     *  {@code fk_doacao_entrevistador}. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entrevistador_id",
            foreignKey = @ForeignKey(name = "fk_doacao_entrevistador")
    )
    private Entrevistador entrevistador;

    /**
     * Itens doados.
     *
     * <p>Cascade e remoção de órfãos vivem aqui porque esta entidade é a raiz do
     * agregado. O lado filho, {@code DoacaoProduto}, não declara cascade algum.</p>
     */
    @OneToMany(
            mappedBy = "doacao",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DoacaoProduto> produtos = new ArrayList<>();
}
