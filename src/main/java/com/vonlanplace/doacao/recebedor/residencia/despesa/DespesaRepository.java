package com.vonlanplace.doacao.recebedor.residencia.despesa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório JPA de {@link Despesa}.
 *
 * <p>A consulta por residência é o filtro central: a somatória apresentada ao
 * entrevistador é a das despesas de uma única residência.</p>
 */
@Repository
public interface DespesaRepository extends JpaRepository<Despesa, UUID> {

    /**
     * Lista as despesas de uma residência, paginadas.
     *
     * @param residenciaId identificador da residência
     * @param pageable     paginação e ordenação
     * @return página de despesas
     */
    Page<Despesa> findByResidencia_Id(UUID residenciaId, Pageable pageable);

    /**
     * Lista todas as despesas de uma residência, na ordem em que foram cadastradas.
     *
     * @param residenciaId identificador da residência
     * @return lista de despesas
     */
    List<Despesa> findByResidencia_Id(UUID residenciaId);
}