package com.vonlanplace.doacao.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório JPA de {@link Cesta}.
 *
 * <p>Mantém a listagem genérica e uma verificação de vínculo, usada por
 * {@code RecebedorService} para recusar a exclusão de um recebedor que ainda tenha
 * cestas entregues. Não há filtro por data ou por recebedor, e o
 * {@code CestaAPIController} não expõe busca.</p>
 */
@Repository
public interface CestaRepository extends JpaRepository<Cesta, UUID> {

    /**
     * Informa se existe alguma cesta vinculada ao recebedor.
     *
     * @param recebedor recebedor consultado
     * @return {@code true} se houver ao menos uma cesta
     */
    boolean existsByRecebedor(Recebedor recebedor);
}
