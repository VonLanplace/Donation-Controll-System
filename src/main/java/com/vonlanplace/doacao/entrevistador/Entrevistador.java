package com.vonlanplace.doacao.entrevistador;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * Usuário da equipe que conduz a entrevista do recebedor.
 *
 * <p><strong>Único usuário do sistema, e o único com senha.</strong> A API não tem
 * autenticação: o campo existe e é gravado em {@code senha_hash}, mas o
 * {@code PasswordEncoder} de {@code config.PasswordConfig} não tem consumidor, porque
 * não existe {@code EntrevistadorService} chamando {@code encode(...)}. Ou seja, o
 * fluxo de criação de entrevistador ainda não foi escrito.</p>
 *
 * <p><strong>{@code senhaHash} não é exposto em nenhuma resposta.</strong> O
 * {@link EntrevistadorMapper} converte para {@link EntrevistadorResponseDTO}, que não
 * tem o campo — é o único caminho em que um entrevistador chega ao JSON, dentro de
 * {@code DoacaoResponseDTO}. A proteção é do DTO, não de
 * {@code @JsonIgnore} na entidade.</p>
 *
 * <p><strong>Identificação de negócio é o e-mail, não o id.</strong> O índice
 * {@code uk_entrevistador_email} existe para isso, e {@code findByEmail} é a consulta
 * prevista para login.</p>
 */
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "entrevistador",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_entrevistador_email",
                columnNames = "email"
        )
)
public class Entrevistador {

    /** Identificador do entrevistador, gerado pelo banco no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    /** Nome do entrevistador. Obrigatório. */
    @Column(name = "nome", nullable = false, length = 100)
    @ToString.Include
    private String nome;

    /** E-mail de acesso. Obrigatório e único. */
    @Column(name = "email", nullable = false, length = 100)
    @ToString.Include
    private String email;

    /**
     * Hash da senha, gravado com BCrypt.
     *
     * <p>Deliberadamente fora do {@code @ToString.Include}: mesmo restrito a campos
     * marcados, deixar o hash fora evita que ele apareça em qualquer log que imprima a
     * entidade.</p>
     */
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;
}
