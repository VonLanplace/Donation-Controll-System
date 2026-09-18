package com.vonlanplace.doacao.recebedor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResidenciaRepository extends JpaRepository<Residencia, UUID> {
}
