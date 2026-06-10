package com.vonlanplace.doacao.repository;

import com.vonlanplace.doacao.entity.MarcaProduto;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Entity
public interface MarcaProdutoRepository extends JpaRepository<MarcaProduto, UUID> {
}
