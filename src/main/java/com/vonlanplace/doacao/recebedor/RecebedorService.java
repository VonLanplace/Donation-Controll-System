package com.vonlanplace.doacao.recebedor;

import com.vonlanplace.doacao.cesta.CestaRepository;
import com.vonlanplace.doacao.doacao.DoacaoRepository;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Regras de negócio de {@link Recebedor}.
 *
 * <p><strong>Identificação por {@code id}, consulta por documentos.</strong> As
 * operações de escrita recebem UUID; as de leitura aceitam CPF, PIX, nome, data de
 * nascimento ou residência.</p>
 *
 * <p><strong>Chaves duplicadas são barradas no serviço, não no banco.</strong> CPF e PIX
 * têm índice único, mas confiar na constraint daria ao cliente um 409 genérico em vez
 * de uma mensagem explicando qual campo colidiu. Os guards private recebem o id a
 * desconsiderar para que a atualização de um recebedor não entre em conflito consigo
 * mesmo.</p>
 *
 * <p><strong>A exclusão é manual, não cascade.</strong> Doações e cestas referenciam o
 * recebedor; a remoção é recusada com 409 enquanto houver qualquer um dos dois.</p>
 */
@Service
public class RecebedorService {

    final private RecebedorRepository recebedorRepository;
    final private RecebedorMapper recebedorMapper;
    final private ResidenciaRepository residenciaRepository;
    final private DoacaoRepository doacaoRepository;
    final private CestaRepository cestaRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param recebedorRepository repositório de recebedores
     * @param recebedorMapper     conversor entidade/DTO
     * @param residenciaRepository repositório de residências
     * @param doacaoRepository    repositório de doações, usado na trava de exclusão
     * @param cestaRepository     repositório de cestas, usado na trava de exclusão
     */
    public RecebedorService(
            RecebedorRepository recebedorRepository,
            RecebedorMapper recebedorMapper,
            ResidenciaRepository residenciaRepository,
            DoacaoRepository doacaoRepository,
            CestaRepository cestaRepository
    ) {
        this.recebedorRepository = recebedorRepository;
        this.recebedorMapper = recebedorMapper;
        this.residenciaRepository = residenciaRepository;
        this.doacaoRepository = doacaoRepository;
        this.cestaRepository = cestaRepository;
    }

    /**
     * Busca a residência referenciada pelo id informado.
     *
     * @param residenciaId identificador da residência
     * @return a residência carregada
     * @throws EntityNotFoundException se a residência não existir
     */
    private Residencia resolveResidencia(UUID residenciaId) {
        return residenciaRepository.findById(residenciaId)
                .orElseThrow(() -> new EntityNotFoundException("Residencia not found: " + residenciaId));
    }

    /**
     * Recusa CPF já usado por outro recebedor.
     *
     * @param cpf      CPF a verificar
     * @param ignoreId recebedor a desconsiderar, ou {@code null} numa criação
     * @throws IllegalStateException se outro recebedor já usa o CPF
     */
    private void guardCpfDuplicado(String cpf, UUID ignoreId) {
        recebedorRepository.findByCpf(cpf)
                .filter(existing -> ignoreId == null || !existing.getId().equals(ignoreId))
                .ifPresent(existing -> {
                    throw new IllegalStateException("CPF já cadastrado para outro recebedor");
                });
    }

    /**
     * Recusa chave PIX já usada por outro recebedor. Valor vazio é ignorado, já que a
     * coluna é opcional.
     *
     * @param pix      chave PIX a verificar
     * @param ignoreId recebedor a desconsiderar, ou {@code null} numa criação
     * @throws IllegalStateException se outro recebedor já usa a chave
     */
    private void guardPixDuplicado(String pix, UUID ignoreId) {
        if (pix == null || pix.isBlank()) return;
        recebedorRepository.findByPix(pix)
                .filter(existing -> ignoreId == null || !existing.getId().equals(ignoreId))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Chave PIX já cadastrada para outro recebedor");
                });
    }

    /**
     * Busca o recebedor pelo identificador.
     *
     * @param id identificador do recebedor
     * @return DTO de resposta
     * @throws EntityNotFoundException se o recebedor não existir
     */
    @Transactional(readOnly = true)
    public RecebedorResponseDTO findById(UUID id) throws EntityNotFoundException {
        return recebedorMapper.toResponseDTO(
                recebedorRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Recebedor not found: " + id)));
    }

    /**
     * Lista todos os recebedores, paginados.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<RecebedorResponseDTO> findAll(Pageable pageable) {
        return recebedorRepository.findAll(pageable)
                .map(recebedorMapper::toResponseDTO);
    }

    /**
     * Lista todos os recebedores, sem paginação, ordenados por nome.
     *
     * <p><strong>Não paginado de propósito.</strong> O select de recebedor e o
     * perfil de uma residência precisam do conjunto inteiro, e o
     * {@code max-page-size=100} do {@code application.properties} cortaria a
     * lista em silêncio se passássemos um {@code Pageable} por aqui — quem não
     * olhasse a última página do select não notaria que recebedores sumiram. O
     * mesmo raciocínio já valeu para {@code ResidenciaService.findAll()}.</p>
     *
     * @return lista de recebedores ordenada por nome
     */
    @Transactional(readOnly = true)
    public List<RecebedorResponseDTO> findAll() {
        return recebedorRepository.findAll(Sort.by("nomeCompleto")).stream()
                .map(recebedorMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca o recebedor pelo CPF.
     *
     * @param cpf CPF sem pontuação
     * @return DTO de resposta
     * @throws EntityNotFoundException se não houver recebedor com esse CPF
     */
    @Transactional(readOnly = true)
    public RecebedorResponseDTO findByCpf(String cpf) throws EntityNotFoundException {
        return recebedorMapper.toResponseDTO(
                recebedorRepository.findByCpf(cpf).orElseThrow(
                        () -> new EntityNotFoundException("Recebedor not found by cpf: " + cpf)));
    }

    /**
     * Busca o recebedor pela chave PIX.
     *
     * @param pix chave PIX
     * @return DTO de resposta
     * @throws EntityNotFoundException se não houver recebedor com essa chave
     */
    @Transactional(readOnly = true)
    public RecebedorResponseDTO findByPix(String pix) throws EntityNotFoundException {
        return recebedorMapper.toResponseDTO(
                recebedorRepository.findByPix(pix).orElseThrow(
                        () -> new EntityNotFoundException("Recebedor not found by pix: " + pix)));
    }

    /**
     * Busca recebedores pelo nome completo exato.
     *
     * @param nomeCompleto nome completo
     * @param pageable     paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<RecebedorResponseDTO> findByNomeCompleto(String nomeCompleto, Pageable pageable) {
        return recebedorRepository.findByNomeCompleto(nomeCompleto, pageable)
                .map(recebedorMapper::toResponseDTO);
    }

    /**
     * Busca recebedores pela data de nascimento exata.
     *
     * @param dataNascimento data de nascimento
     * @param pageable       paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<RecebedorResponseDTO> findByDataNascimento(LocalDate dataNascimento, Pageable pageable) {
        return recebedorRepository.findByDataNascimento(dataNascimento, pageable)
                .map(recebedorMapper::toResponseDTO);
    }

    /**
     * Lista os moradores de uma residência. Valida a residência antes de consultar, para
     * que uma residência inexistente devolva 404 e não uma lista vazia.
     *
     * @param residenciaId identificador da residência
     * @param pageable     paginação e ordenação
     * @return página de DTOs dos moradores
     * @throws EntityNotFoundException se a residência não existir
     */
    @Transactional(readOnly = true)
    public Page<RecebedorResponseDTO> findByResidencia(UUID residenciaId, Pageable pageable)
            throws EntityNotFoundException {
        return recebedorRepository.findByResidencia_Id(resolveResidencia(residenciaId).getId(), pageable)
                .map(recebedorMapper::toResponseDTO);
    }

    /**
     * Cadastra um recebedor e o liga à residência informada.
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta do recebedor criado
     * @throws EntityNotFoundException se a residência informada não existir
     * @throws IllegalStateException    se o CPF ou a chave PIX já estiverem em uso
     */
    @Transactional
    public RecebedorResponseDTO create(RecebedorCreateDTO createDTO) throws EntityNotFoundException {
        guardCpfDuplicado(createDTO.cpf(), null);
        guardPixDuplicado(createDTO.pix(), null);

        Recebedor recebedor = recebedorMapper.toEntity(createDTO);
        recebedor.setResidencia(resolveResidencia(createDTO.residenciaId()));
        Recebedor saved = recebedorRepository.save(recebedor);
        return recebedorMapper.toResponseDTO(saved);
    }

    /**
     * Substitui todos os dados do recebedor, incluindo a residência.
     *
     * @param id        identificador do recebedor
     * @param updateDTO dados de entrada
     * @return DTO de resposta do recebedor atualizado
     * @throws EntityNotFoundException se o recebedor ou a residência informada não existirem
     * @throws IllegalStateException    se o CPF ou a chave PIX já pertencerem a outro recebedor
     */
    @Transactional
    public RecebedorResponseDTO update(UUID id, RecebedorUpdateDTO updateDTO)
            throws EntityNotFoundException {
        Recebedor recebedor = recebedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recebedor not found: " + id));

        guardCpfDuplicado(updateDTO.cpf(), recebedor.getId());
        guardPixDuplicado(updateDTO.pix(), recebedor.getId());

        recebedor.setResidencia(resolveResidencia(updateDTO.residenciaId()));
        recebedorMapper.updateEntityFromDTO(updateDTO, recebedor);
        Recebedor saved = recebedorRepository.save(recebedor);
        return recebedorMapper.toResponseDTO(saved);
    }

    /**
     * Exclui o recebedor, recusando a remoção se houver doações ou cestas vinculadas.
     *
     * @param id identificador do recebedor
     * @throws EntityNotFoundException se o recebedor não existir
     * @throws IllegalStateException    se houver doações ou cestas vinculadas
     */
    @Transactional
    public void delete(UUID id) throws EntityNotFoundException {
        Recebedor recebedor = recebedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recebedor not found: " + id));

        if (doacaoRepository.existsByRecebedor(recebedor)) {
            throw new IllegalStateException("Não é possível excluir recebedor com doações vinculadas");
        }
        if (cestaRepository.existsByRecebedor(recebedor)) {
            throw new IllegalStateException("Não é possível excluir recebedor com cestas vinculadas");
        }

        recebedorRepository.delete(recebedor);
    }
}
