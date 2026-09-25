package com.vonlanplace.doacao.recebedor.residencia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResidenciaRepository extends JpaRepository<Residencia, UUID> {
    Page<Residencia> findByLogradouro(String logradouro, Pageable pageable);

    Page<Residencia> findByCep(String cep, Pageable pageable);

    Page<Residencia> findByCidade(String cidade, Pageable pageable);

    Page<Residencia> findByBairro(String bairro, Pageable pageable);

}
