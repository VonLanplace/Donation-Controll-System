package com.vonlanplace.doacao.repository;

import com.vonlanplace.doacao.entity.Recebedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecebedorRepository extends JpaRepository<Recebedor, UUID> {
}
