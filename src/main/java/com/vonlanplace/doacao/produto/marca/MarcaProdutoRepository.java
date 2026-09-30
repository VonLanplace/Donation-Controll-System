package com.vonlanplace.doacao.produto.marca;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA de {@link MarcaProduto}.
 *
 * <p>A consulta de negócio é por {@code nome}, não por {@code id}.</p>
 */
@Repository
public interface MarcaProdutoRepository extends JpaRepository<MarcaProduto, UUID> {

    /**
     * Busca a marca pelo nome exato.
     *
     * @param nome nome da marca
     * @return a marca, ou {@link Optional#empty()} se não existir
     */
    Optional<MarcaProduto> findByNome(String nome);
}
