package com.vonlanplace.doacao.recebedor.dependente;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Regras de negócio de {@link Dependente}.
 *
 * <p>CRUD simples de sub-registro: criar, editar, listar. Não há soma nem
 * regra de conflito — o dependente é dado de entrevista, não dado financeiro.</p>
 *
 * <p><strong>A exclusão em cascata vem do pai.</strong> O dependente morre com o
 * recebedor; {@code delete(UUID)} existe para remover só o dependente quando o
 * recebedor continua de pé.</p>
 */
@Service
public class DependenteService {

    final private DependenteRepository dependenteRepository;
    final private DependenteMapper dependenteMapper;
    final private RecebedorRepository recebedorRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param dependenteRepository repositório de dependentes
     * @param dependenteMapper     conversor entidade/DTO
     * @param recebedorRepository  repositório de recebedores, para resolver o vínculo
     */
    public DependenteService(
            DependenteRepository dependenteRepository,
            DependenteMapper dependenteMapper,
            RecebedorRepository recebedorRepository
    ) {
        this.dependenteRepository = dependenteRepository;
        this.dependenteMapper = dependenteMapper;
        this.recebedorRepository = recebedorRepository;
    }

    /**
     * Busca o recebedor referenciado pelo id informado.
     *
     * @param recebedorId identificador do recebedor
     * @return o recebedor carregado
     * @throws EntityNotFoundException se o recebedor não existir
     */
    private Recebedor resolveRecebedor(UUID recebedorId) {
        return recebedorRepository.findById(recebedorId)
                .orElseThrow(() -> new EntityNotFoundException("Recebedor not found: " + recebedorId));
    }

    /**
     * Busca o dependente pelo identificador.
     *
     * @param id identificador do dependente
     * @return DTO de resposta
     * @throws EntityNotFoundException se o dependente não existir
     */
    @Transactional(readOnly = true)
    public DependenteResponseDTO findById(UUID id) throws EntityNotFoundException {
        return dependenteMapper.toResponseDTO(
                dependenteRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Dependente not found: " + id)));
    }

    /**
     * Lista todos os dependentes, paginados.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<DependenteResponseDTO> findAll(Pageable pageable) {
        return dependenteRepository.findAll(pageable)
                .map(dependenteMapper::toResponseDTO);
    }

    /**
     * Lista os dependentes de um recebedor, paginados.
     *
     * @param recebedorId identificador do recebedor
     * @param pageable    paginação e ordenação
     * @return página de DTOs dos dependentes
     * @throws EntityNotFoundException se o recebedor não existir
     */
    @Transactional(readOnly = true)
    public Page<DependenteResponseDTO> findByRecebedor(UUID recebedorId, Pageable pageable)
            throws EntityNotFoundException {
        return dependenteRepository.findByRecebedor_Id(resolveRecebedor(recebedorId).getId(), pageable)
                .map(dependenteMapper::toResponseDTO);
    }

    /**
     * Lista os dependentes de um recebedor, na ordem do cadastro, sem paginação.
     *
     * <p>Alimenta o perfil do recebedor, que mostra "quantos filhos" de uma vez;
     * a família raramente tem mais que uma página de dependentes.</p>
     *
     * @param recebedorId identificador do recebedor
     * @return lista de DTOs dos dependentes
     * @throws EntityNotFoundException se o recebedor não existir
     */
    @Transactional(readOnly = true)
    public List<DependenteResponseDTO> findByRecebedor(UUID recebedorId)
            throws EntityNotFoundException {
        resolveRecebedor(recebedorId);
        return dependenteRepository.findByRecebedor_Id(recebedorId).stream()
                .map(dependenteMapper::toResponseDTO)
                .toList();
    }

    /**
     * Cadastra um dependente e o liga ao recebedor informado.
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta do dependente criado
     * @throws EntityNotFoundException se o recebedor informado não existir
     */
    @Transactional
    public DependenteResponseDTO create(DependenteCreateDTO createDTO) throws EntityNotFoundException {
        Dependente dependente = dependenteMapper.toEntity(createDTO);
        dependente.setRecebedor(resolveRecebedor(createDTO.recebedorId()));
        Dependente saved = dependenteRepository.save(dependente);
        return dependenteMapper.toResponseDTO(saved);
    }

    /**
     * Substitui todos os dados do dependente, incluindo o recebedor.
     *
     * @param id        identificador do dependente
     * @param updateDTO dados de entrada
     * @return DTO de resposta do dependente atualizado
     * @throws EntityNotFoundException se o dependente ou o recebedor informado não existirem
     */
    @Transactional
    public DependenteResponseDTO update(UUID id, DependenteUpdateDTO updateDTO)
            throws EntityNotFoundException {
        Dependente dependente = dependenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dependente not found: " + id));
        dependente.setRecebedor(resolveRecebedor(updateDTO.recebedorId()));
        dependenteMapper.updateEntityFromDTO(updateDTO, dependente);
        Dependente saved = dependenteRepository.save(dependente);
        return dependenteMapper.toResponseDTO(saved);
    }

    /**
     * Exclui um dependente isolado.
     *
     * <p>O dependente também morre em cascata quando o recebedor é removido via
     * {@code RecebedorService}; este método é o caminho usado quando apenas o
     * dependente está errado e o recebedor continua de pé.</p>
     *
     * @param id identificador do dependente
     * @throws EntityNotFoundException se o dependente não existir
     */
    @Transactional
    public void delete(UUID id) throws EntityNotFoundException {
        Dependente dependente = dependenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dependente not found: " + id));
        dependenteRepository.delete(dependente);
    }
}