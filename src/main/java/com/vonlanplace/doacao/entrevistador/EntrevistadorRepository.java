package com.vonlanplace.doacao.entrevistador;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EntrevistadorRepository extends JpaRepository<Entrevistador, UUID> {

    Entrevistador findByEmail(String email);

    List<Entrevistador> findByNome(String nome);
}