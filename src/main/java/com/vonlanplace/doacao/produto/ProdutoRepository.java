package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    Optional<Produto> findByCodigoBarras(String codigoBarras);

    List<Produto> findAllByTipoProduto(TipoProduto tipoProduto);

    List<Produto> findAllByMarcaProduto(MarcaProduto marcaProduto);

    List<Produto> findAll();

}
