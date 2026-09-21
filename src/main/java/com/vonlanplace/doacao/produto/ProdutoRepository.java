package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    Page<Produto> findByCodigoBarras(String codigoBarras, Pageable pageable);

    Page<Produto> findAllByTipoProduto(TipoProduto tipoProduto, Pageable pageable);

    Page<Produto> findAllByMarcaProduto(MarcaProduto marcaProduto, Pageable pageable);

}
