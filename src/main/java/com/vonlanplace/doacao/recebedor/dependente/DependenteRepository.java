package com.vonlanplace.doacao.recebedor.dependente;

import com.vonlanplace.doacao.recebedor.Recebedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório JPA de {@link Dependente}.
 *
 * <p>Os dependentes pertencem a um recebedor; a consulta por dono é o
 * filtro central, usado pela listagem e pelo perfil do recebedor.</p>
 */
@Repository
public interface DependenteRepository extends JpaRepository<Dependente, UUID> {

    /**
     * Lista os dependentes de um recebedor, paginados.
     *
     * @param recebedorId identificador do recebedor
     * @param pageable    paginação e ordenação
     * @return página de dependentes
     */
    Page<Dependente> findByRecebedor_Id(UUID recebedorId, Pageable pageable);

    /**
     * Lista todos os dependentes de um recebedor, na ordem em que foram cadastrados.
     *
     * <p>Sem paginação de propósito: o perfil do recebedor precisa saber quantos
     * são e a listagem é por definição pequena (filhos da família).</p>
     *
     * @param recebedorId identificador do recebedor
     * @return lista de dependentes
     */
    List<Dependente> findByRecebedor_Id(UUID recebedorId);
}