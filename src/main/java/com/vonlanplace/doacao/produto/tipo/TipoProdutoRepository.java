package com.vonlanplace.doacao.produto.tipo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TipoProdutoRepository extends JpaRepository<TipoProduto, UUID> {
    Optional<TipoProduto> findByNome(String nome);
}
