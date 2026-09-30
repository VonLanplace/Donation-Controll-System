package com.vonlanplace.doacao.produto.tipo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA de {@link TipoProduto}.
 *
 * <p>A consulta de negócio é por {@code nome}, não por {@code id}.</p>
 */
@Repository
public interface TipoProdutoRepository extends JpaRepository<TipoProduto, UUID> {

    /**
     * Busca o tipo pelo nome exato.
     *
     * @param nome nome do tipo
     * @return o tipo, ou {@link Optional#empty()} se não existir
     */
    Optional<TipoProduto> findByNome(String nome);
}
