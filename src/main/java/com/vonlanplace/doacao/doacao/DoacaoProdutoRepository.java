package com.vonlanplace.doacao.doacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoacaoProdutoRepository extends JpaRepository<DoacaoProduto, DoacaoProdutoId> {
}
