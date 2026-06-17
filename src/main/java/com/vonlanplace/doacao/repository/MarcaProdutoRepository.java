package com.vonlanplace.doacao.repository;

import com.vonlanplace.doacao.entity.MarcaProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MarcaProdutoRepository extends JpaRepository<MarcaProduto, UUID> {
}
