package com.vonlanplace.doacao.recebedor.residencia;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório JPA de {@link Residencia}.
 *
 * <p>Não há índice único no endereço: duas residências podem ter o mesmo CEP e a mesma
 * rua, por exemplo quando o mesmo prédio atende a mais de uma família. As consultas são
 * por igualdade exata de cada campo, e combiná-las exigiria um {@code Specification}, que
 * este projeto não usa.</p>
 */
@Repository
public interface ResidenciaRepository extends JpaRepository<Residencia, UUID> {

    /**
     * Busca por logradouro exato.
     *
     * @param logradouro logradouro
     * @param pageable    paginação e ordenação
     * @return página de residências
     */
    Page<Residencia> findByLogradouro(String logradouro, Pageable pageable);

    /**
     * Busca por CEP exato, sem hífen.
     *
     * @param cep      CEP
     * @param pageable paginação e ordenação
     * @return página de residências
     */
    Page<Residencia> findByCep(String cep, Pageable pageable);

    /**
     * Busca por cidade exata.
     *
     * @param cidade   cidade
     * @param pageable paginação e ordenação
     * @return página de residências
     */
    Page<Residencia> findByCidade(String cidade, Pageable pageable);

    /**
     * Busca por bairro exato.
     *
     * @param bairro   bairro
     * @param pageable paginação e ordenação
     * @return página de residências
     */
    Page<Residencia> findByBairro(String bairro, Pageable pageable);

}
