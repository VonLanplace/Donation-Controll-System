package com.vonlanplace.doacao.repository;

import com.vonlanplace.doacao.entity.TipoProduto;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Entity
public interface TipoProdutoRepository extends JpaRepository<TipoProduto, UUID> {
}
