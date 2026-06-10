package com.vonlanplace.doacao.repository;

import com.vonlanplace.doacao.entity.DoacaoProduto;
import com.vonlanplace.doacao.entity.DoacaoProdutoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoacaoProdutoRepository extends JpaRepository<DoacaoProduto, DoacaoProdutoId> {
}
