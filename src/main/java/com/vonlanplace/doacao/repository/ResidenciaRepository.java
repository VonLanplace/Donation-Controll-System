package com.vonlanplace.doacao.repository;

import com.vonlanplace.doacao.entity.Residencia;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Entity
public interface ResidenciaRepository extends JpaRepository<Residencia, UUID> {
}
