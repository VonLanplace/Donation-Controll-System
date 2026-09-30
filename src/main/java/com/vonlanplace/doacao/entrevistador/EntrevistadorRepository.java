package com.vonlanplace.doacao.entrevistador;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA de {@link Entrevistador}.
 *
 * <p><strong>Consultas prepare para um fluxo de login que não existe ainda.</strong>
 * {@link #findByEmail(String)} é a busca de credencial e {@link #findByNome(String)} a
 * de listagem por nome. Nenhuma das duas é chamada hoje: o serviço de entrevistador não
 * foi escrito, e o único consumidor deste repositório é
 * {@code DoacaoService.resolveEntrevistador}, que resolve por id.</p>
 *
 * <p>{@code findByNome} devolve {@code List} e não {@code Page}, sem paginação. É
 * diferente de todos os outros repositórios do projeto e provavelmente seja corrigido
 * junto com o serviço.</p>
 */
@Repository
public interface EntrevistadorRepository extends JpaRepository<Entrevistador, UUID> {

    /**
     * Busca pelo e-mail exato, que é a chave de login.
     *
     * @param email e-mail de acesso
     * @return o entrevistador, ou vazio se não existir
     */
    Optional<Entrevistador> findByEmail(String email);

    /**
     * Lista todos os entrevistadores com o nome informado.
     *
     * @param nome nome exato
     * @return lista de entrevistadores, sem paginação
     */
    List<Entrevistador> findByNome(String nome);
}
