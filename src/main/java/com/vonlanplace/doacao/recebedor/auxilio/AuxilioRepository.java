package com.vonlanplace.doacao.recebedor.auxilio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório JPA de {@link Auxilio}.
 *
 * <p>A consulta por recebedor é o filtro central: a somatória apresentada ao
 * entrevistador é a dos auxílios de um único recebedor.</p>
 */
@Repository
public interface AuxilioRepository extends JpaRepository<Auxilio, UUID> {

    /**
     * Lista os auxílios de um recebedor, paginados.
     *
     * @param recebedorId identificador do recebedor
     * @param pageable    paginação e ordenação
     * @return página de auxílios
     */
    Page<Auxilio> findByRecebedor_Id(UUID recebedorId, Pageable pageable);
}