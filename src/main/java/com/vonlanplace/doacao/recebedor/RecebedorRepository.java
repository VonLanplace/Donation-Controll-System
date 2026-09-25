package com.vonlanplace.doacao.recebedor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecebedorRepository extends JpaRepository<Recebedor, UUID> {

    Optional<Recebedor> findByCpf(String cpf);

    Optional<Recebedor> findByPix(String pix);

    Optional<Recebedor> findByRgNumAndRgEmissor(String rgNum, OrgaoEmissorRG rgEmissor);

    Page<Recebedor> findDataNascimento(LocalDate dataNascimento, Pageable pageable);

    Page<Recebedor> findByNomeCompleto(String cep, Pageable pageable);
}
