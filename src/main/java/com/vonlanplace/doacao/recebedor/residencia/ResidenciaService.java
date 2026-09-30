package com.vonlanplace.doacao.recebedor.residencia;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Regras de negócio de {@link Residencia}.
 *
 * <p>Serviço mais simples do projeto: CRUD puro por {@code id}, com quatro buscas por
 * campo do endereço. Nenhuma regra de conflito, porque endereço não tem chave única.</p>
 *
 * <p><strong>A exclusão não é validada aqui.</strong> Se a residência tiver moradores, a
 * remoção violaria a FK de {@code Recebedor.residencia} e o cliente receberia 409 com a
 * mensagem genérica "Conflito de Dados", vindo do
 * {@code GlobalExceptionHandler}. Outros serviços, como
 * {@code MarcaProdutoService} e {@code RecebedorService}, checam a dependência antes e
 * dão mensagem específica; aqui a checagem não existe.</p>
 */
@Service
public class ResidenciaService {

    final private ResidenciaRepository residenciaRepository;
    final private ResidenciaMapper residenciaMapper;

    /**
     * Construtor com injeção de dependências.
     *
     * @param residenciaRepository repositório de residências
     * @param residenciaMapper      conversor entidade/DTO
     */
    public ResidenciaService(
            ResidenciaRepository residenciaRepository,
            ResidenciaMapper residenciaMapper
    ) {
        this.residenciaRepository = residenciaRepository;
        this.residenciaMapper = residenciaMapper;
    }

    /**
     * Busca a residência pelo identificador.
     *
     * @param id identificador da residência
     * @return DTO de resposta
     * @throws EntityNotFoundException se a residência não existir
     */
    @Transactional(readOnly = true)
    public ResidenciaResponseDTO findById(UUID id) throws EntityNotFoundException {
        return residenciaMapper.toResponseDTO(
                residenciaRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Residencia not found: " + id)));
    }

    /**
     * Lista todas as residências, paginadas.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<ResidenciaResponseDTO> findAll(Pageable pageable) {
        return residenciaRepository.findAll(pageable)
                .map(residenciaMapper::toResponseDTO);
    }

    /**
     * Lista todas as residências, sem paginação.
     *
     * <p>Alimenta o {@code <select>} do formulário de recebedor, e precisa do
     * conjunto inteiro pelo mesmo motivo de {@code listaNomes()} de marca e
     * tipo: {@code max-page-size=100} truncaria em silêncio.</p>
     *
     * @return lista de residências ordenada por cidade
     */
    @Transactional(readOnly = true)
    public List<ResidenciaResponseDTO> findAll() {
        return residenciaRepository.findAll(Sort.by("cidade")).stream()
                .map(residenciaMapper::toResponseDTO)
                .toList();
    }

    /**
     * Busca por logradouro exato.
     *
     * @param logradouro logradouro
     * @param pageable   paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<ResidenciaResponseDTO> findByLogradouro(String logradouro, Pageable pageable) {
        return residenciaRepository.findByLogradouro(logradouro, pageable)
                .map(residenciaMapper::toResponseDTO);
    }

    /**
     * Busca por CEP exato.
     *
     * @param cep      CEP sem hífen
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<ResidenciaResponseDTO> findByCep(String cep, Pageable pageable) {
        return residenciaRepository.findByCep(cep, pageable)
                .map(residenciaMapper::toResponseDTO);
    }

    /**
     * Busca por cidade exata.
     *
     * @param cidade   cidade
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<ResidenciaResponseDTO> findByCidade(String cidade, Pageable pageable) {
        return residenciaRepository.findByCidade(cidade, pageable)
                .map(residenciaMapper::toResponseDTO);
    }

    /**
     * Busca por bairro exato.
     *
     * @param bairro   bairro
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<ResidenciaResponseDTO> findByBairro(String bairro, Pageable pageable) {
        return residenciaRepository.findByBairro(bairro, pageable)
                .map(residenciaMapper::toResponseDTO);
    }

    /**
     * Cadastra uma residência, sem moradores.
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta da residência criada
     */
    @Transactional
    public ResidenciaResponseDTO create(ResidenciaCreateDTO createDTO) {
        Residencia residencia = residenciaMapper.toEntity(createDTO);
        Residencia saved = residenciaRepository.save(residencia);
        return residenciaMapper.toResponseDTO(saved);
    }

    /**
     * Substitui todos os dados da residência. Os moradores vinculados não são alterados.
     *
     * @param id        identificador da residência
     * @param updateDTO dados de entrada
     * @return DTO de resposta da residência atualizada
     * @throws EntityNotFoundException se a residência não existir
     */
    @Transactional
    public ResidenciaResponseDTO update(UUID id, ResidenciaUpdateDTO updateDTO)
            throws EntityNotFoundException {
        Residencia residencia = residenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Residencia not found: " + id));
        residenciaMapper.updateEntityFromDTO(updateDTO, residencia);
        Residencia saved = residenciaRepository.save(residencia);
        return residenciaMapper.toResponseDTO(saved);
    }

    /**
     * Exclui a residência. Se houver moradores vinculados, a violação de FK é
     * traduzida em 409 pelo {@code GlobalExceptionHandler}, com mensagem genérica.
     *
     * @param id identificador da residência
     * @throws EntityNotFoundException se a residência não existir
     */
    @Transactional
    public void delete(UUID id) throws EntityNotFoundException {
        Residencia residencia = residenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Residencia not found: " + id));
        residenciaRepository.delete(residencia);
    }
}
