package com.vonlanplace.doacao.produto.marca;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarcaProdutoRepository extends JpaRepository<MarcaProduto, UUID> {
    Optional<MarcaProduto> findByNome(String nome);
}
