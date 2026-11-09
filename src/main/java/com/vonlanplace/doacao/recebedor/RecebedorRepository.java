package com.vonlanplace.doacao.recebedor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA de {@link Recebedor}.
 *
 * <p>Além da busca por id herdada de {@code JpaRepository}, expõe consultas pelos
 * documentos (CPF, PIX, RG), por nome e por data de nascimento, que são os filtros
 * usados por {@link RecebedorService} e pelo controller.</p>
 *
 * <p><strong>Atenção ao nome derivado.</strong> O padrão do Spring Data é
 * {@code findBy<Propriedade>} e a travessia de relação usa {@code _}:
 * {@code findByResidencia_Id} é o nome correto para
 * {@code findByResidenciaId}, que o Spring Data rejeitaria em tempo de inicialização.
 * Omitir o {@code By} ou o {@code _} derruba o contexto na hora do startup.</p>
 */
@Repository
public interface RecebedorRepository extends JpaRepository<Recebedor, UUID> {

    /**
     * Busca pelo CPF exato.
     *
     * @param cpf CPF sem pontuação
     * @return o recebedor, ou vazio se não existir
     */
    Optional<Recebedor> findByCpf(String cpf);

    /**
     * Busca pela chave PIX exata.
     *
     * @param pix chave PIX
     * @return o recebedor, ou vazio se não existir
     */
    Optional<Recebedor> findByPix(String pix);

    /**
     * Busca pelo par número do RG e órgão emissor.
     *
     * @param rgNum    número do RG
     * @param rgEmissor órgão emissor
     * @return o recebedor, ou vazio se não existir
     */
    Optional<Recebedor> findByRgNumAndRgEmissor(String rgNum, OrgaoEmissorRG rgEmissor);

    /**
     * Busca pelo número do RG, ignorando maiúsculas e minúsculas.
     *
     * <p>O RG não é único no cadastro, por isso a consulta devolve lista. A tela de
     * consulta usa o resultado para pedir a escolha do recebedor quando houver mais de
     * um.</p>
     *
     * @param rgNum número do RG
     * @return recebedores com o RG informado
     */
    List<Recebedor> findByRgNumIgnoreCase(String rgNum);

    /**
     * Busca por data de nascimento exata.
     *
     * @param dataNascimento data de nascimento
     * @param pageable       paginação e ordenação
     * @return página de recebedores
     */
    Page<Recebedor> findByDataNascimento(LocalDate dataNascimento, Pageable pageable);

    /**
     * Busca por nome completo exato.
     *
     * @param nomeCompleto nome completo
     * @param pageable     paginação e ordenação
     * @return página de recebedores
     */
    Page<Recebedor> findByNomeCompleto(String nomeCompleto, Pageable pageable);

    /**
     * Busca todos os moradores de uma residência.
     *
     * @param residenciaId identificador da residência
     * @param pageable     paginação e ordenação
     * @return página de recebedores da residência
     */
    Page<Recebedor> findByResidencia_Id(UUID residenciaId, Pageable pageable);
}
