package com.vonlanplace.doacao.repository;

import com.vonlanplace.doacao.entity.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoacaoRepository extends JpaRepository<Doacao, UUID> {
}
