package com.vonlanplace.doacao.produto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MarcaProdutoRepository extends JpaRepository<MarcaProduto, UUID> {
}
