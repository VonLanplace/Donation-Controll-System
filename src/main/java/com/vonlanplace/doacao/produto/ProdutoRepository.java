package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório JPA de {@link Produto}.
 *
 * <p>Todas as consultas paginadas trazem {@code marcaProduto} e {@code tipoProduto} em
 * {@code JOIN} por {@link EntityGraph}, para que a conversão para
 * {@link ProdutoResponseDTO} não dispare uma consulta por linha.</p>
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

    /**
     * Busca produtos pelo código de barras exato.
     *
     * @param codigoBarras código de barras procurado
     * @param pageable     configuração de paginação e ordenação
     * @return página de produtos com o código informado
     */
    @EntityGraph(attributePaths = {"marcaProduto", "tipoProduto"})
    Page<Produto> findByCodigoBarras(String codigoBarras, Pageable pageable);

    /**
     * Busca produtos de um tipo específico.
     *
     * @param tipoProduto tipo do produto
     * @param pageable    configuração de paginação e ordenação
     * @return página de produtos do tipo informado
     */
    @EntityGraph(attributePaths = {"marcaProduto", "tipoProduto"})
    Page<Produto> findAllByTipoProduto(TipoProduto tipoProduto, Pageable pageable);

    /**
     * Busca produtos de uma marca específica.
     *
     * @param marcaProduto marca do produto
     * @param pageable     configuração de paginação e ordenação
     * @return página de produtos da marca informada
     */
    @EntityGraph(attributePaths = {"marcaProduto", "tipoProduto"})
    Page<Produto> findAllByMarcaProduto(MarcaProduto marcaProduto, Pageable pageable);

    /**
     * Informa se existe ao menos um produto da marca informada.
     *
     * <p>Usado como trava de exclusão: uma marca com produtos vinculados não pode ser
     * removida.</p>
     *
     * @param marcaProduto marca verificada
     * @return {@code true} se algum produto referencia a marca
     */
    boolean existsByMarcaProduto(MarcaProduto marcaProduto);

    /**
     * Informa se existe ao menos um produto do tipo informado.
     *
     * <p>Usado como trava de exclusão: um tipo com produtos vinculados não pode ser
     * removido.</p>
     *
     * @param tipoProduto tipo verificado
     * @return {@code true} se algum produto referencia o tipo
     */
    boolean existsByTipoProduto(TipoProduto tipoProduto);
}
