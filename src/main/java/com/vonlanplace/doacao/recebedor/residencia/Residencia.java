package com.vonlanplace.doacao.recebedor.residencia;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.residencia.despesa.Despesa;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Endereço de um ou mais {@link Recebedor}s.
 *
 * <p><strong>A relação é bidirecional e o lado dono é o recebedor.</strong>
 * {@code Recebedor.residencia} carrega a coluna {@code residencia_id} com
 * {@code optional = false} e FK nomeada {@code fk_recebedor_residencia}. Esta classe é
 * só o lado inverso, via {@code mappedBy = "residencia"}.</p>
 *
 * <p><strong>Por isso a exclusão aqui não é travada em código.</strong> Sem cascade
 * configurado, remover uma residência com moradores viola a FK e o
 * {@code GlobalExceptionHandler} traduz a violação para 409 com a mensagem genérica
 * "Conflito de Dados", diferente da mensagem específica que
 * {@code RecebedorService} e {@code MarcaProdutoService} produzem. A assimetria é
 * conhecida e não foi corrigida.</p>
 *
 * <p>{@code @Data} é proibido nesta classe: o par com {@code Recebedor.recebedores}
 * recursaria no {@code toString}.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "residencia")
public class Residencia {

    /** Identificador da residência, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Logradouro da rua. Obrigatório. */
    @Column(name = "logradouro", nullable = false, length = 150)
    @ToString.Include
    private String logradouro;

    /** Número do endereço. Obrigatório, textual para aceitar "S/N" e blocos. */
    @Column(name = "numero", nullable = false, length = 20)
    @ToString.Include
    private String numero;

    /** CEP sem hífen. Obrigatório. */
    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    /** Complemento do endereço. Opcional. */
    @Column(name = "complemento", length = 100)
    private String complemento;

    /** Cidade. Obrigatória. */
    @Column(name = "cidade", nullable = false, length = 100)
    @ToString.Include
    private String cidade;

    /** Bairro. Obrigatório. */
    @Column(name = "bairro", nullable = false, length = 100)
    @ToString.Include
    private String bairro;

    /** Quantidade de moradores do endereço. Não negativo. */
    @Column(name = "num_moradores", nullable = false)
    @PositiveOrZero
    private int numMoradores;

    /** Quantidade de trabalhadores na residência. Não negativo. */
    @Column(name = "num_trabalhadores", nullable = false)
    @PositiveOrZero
    private int numTrabalhadores;

    /** Perfil de ocupação dos moradores. Obrigatório. */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ocupacao", nullable = false, length = 30)
    private TipoOcupacao tipoOcupacao;

    /** Tipo de moradia. Obrigatório. */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residencia", nullable = false, length = 30)
    private TipoResidencia tipoResidencia;

    /** Moradores deste endereço. Lado inverso de {@code Recebedor.residencia}. */
    @OneToMany(mappedBy = "residencia", fetch = FetchType.LAZY)
    private List<Recebedor> recebedores = new ArrayList<>();

    /** Despesas registradas da residência. Lado dono: a despesa morre com a residência. */
    @OneToMany(mappedBy = "residencia", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Despesa> despesas = new ArrayList<>();
}
