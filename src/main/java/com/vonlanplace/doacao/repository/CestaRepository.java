package com.vonlanplace.doacao.repository;

import com.vonlanplace.doacao.entity.Cesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CestaRepository extends JpaRepository<Cesta, UUID> {
}
