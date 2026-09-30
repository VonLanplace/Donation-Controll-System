package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.recebedor.Recebedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório JPA de {@link Doacao}.
 *
 * <p>Mantém a listagem genérica e uma única verificação de vínculo, usada por
 * {@code RecebedorService} para recusar a exclusão de um recebedor que ainda tenha
 * doações. A mesma trava para cestas fica em {@code CestaRepository}.</p>
 */
@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, UUID> {

    /**
     * Informa se existe alguma doação vinculada ao recebedor.
     *
     * @param recebedor recebedor consultado
     * @return {@code true} se houver ao menos uma doação
     */
    boolean existsByRecebedor(Recebedor recebedor);
}
