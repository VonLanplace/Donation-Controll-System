package com.vonlanplace.doacao.recebedor.cesta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA de {@link RecebimentoCesta}.
 *
 * <p><strong>A unicidade mensal é do banco.</strong> O serviço também consulta antes
 * de gravar para devolver uma mensagem de negócio, mas a constraint
 * {@code uk_recebimento_cesta_recebedor_mes} é a última linha de defesa contra duas
 * submissões simultâneas.</p>
 */
@Repository
public interface RecebimentoCestaRepository extends JpaRepository<RecebimentoCesta, UUID> {

    /**
     * Busca o registro de um recebedor em um mês.
     *
     * @param recebedorId    identificador do recebedor
     * @param dataReferencia primeiro dia do mês
     * @return o registro, ou vazio se não existir
     */
    Optional<RecebimentoCesta> findByRecebedor_IdAndDataReferencia(UUID recebedorId, LocalDate dataReferencia);

    /**
     * Lista o histórico de um recebedor, do mês mais recente para o mais antigo.
     *
     * @param recebedorId identificador do recebedor
     * @return histórico completo
     */
    List<RecebimentoCesta> findByRecebedor_IdOrderByDataReferenciaDesc(UUID recebedorId);

    /**
     * Busca o último registro de um recebedor com determinado status.
     *
     * @param recebedorId identificador do recebedor
     * @param status      status desejado
     * @return o registro mais recente, ou vazio
     */
    Optional<RecebimentoCesta> findFirstByRecebedor_IdAndStatusOrderByDataReferenciaDesc(
            UUID recebedorId, StatusRecebimento status);

    /**
     * Busca os registros de vários recebedores em um único mês.
     *
     * <p>Usado pela listagem para montar a coluna "Cesta" sem uma consulta por linha.</p>
     *
     * @param dataReferencia primeiro dia do mês
     * @param recebedorIds   identificadores dos recebedores da página
     * @return registros encontrados
     */
    List<RecebimentoCesta> findByDataReferenciaAndRecebedor_IdIn(
            LocalDate dataReferencia, Collection<UUID> recebedorIds);

    /**
     * Busca os registros de um status em um intervalo de meses.
     *
     * @param status status desejado
     * @param inicio primeiro dia do mês inicial, inclusive
     * @param fim    último dia do mês final, inclusive
     * @return registros ordenados por recebedor e mês
     */
    List<RecebimentoCesta> findByStatusAndDataReferenciaBetweenOrderByRecebedor_IdAscDataReferenciaAsc(
            StatusRecebimento status, LocalDate inicio, LocalDate fim);
}
