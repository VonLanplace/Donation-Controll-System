package com.vonlanplace.doacao.recebedor.residencia.despesa;

import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Regras de negócio de {@link Despesa}.
 *
 * <p>CRUD simples de sub-registro: criar, editar, listar e somar as despesas de
 * uma residência. O valor monetário nunca é arredondado no caminho — o somador
 * devolve escala 2 apenas para exibição estável, sem perder centavos.</p>
 *
* <p><strong>A exclusão em cascata vem do pai.</strong> A despesa morre com a
     * residência; {@code delete(UUID)} existe para remover só a despesa quando a
     * residência continua de pé.</p>
     */
@Service
public class DespesaService {

    final private DespesaRepository despesaRepository;
    final private DespesaMapper despesaMapper;
    final private ResidenciaRepository residenciaRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param despesaRepository   repositório de despesas
     * @param despesaMapper       conversor entidade/DTO
     * @param residenciaRepository repositório de residências, para resolver o vínculo
     */
    public DespesaService(
            DespesaRepository despesaRepository,
            DespesaMapper despesaMapper,
            ResidenciaRepository residenciaRepository
    ) {
        this.despesaRepository = despesaRepository;
        this.despesaMapper = despesaMapper;
        this.residenciaRepository = residenciaRepository;
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
     * Busca a despesa pelo identificador.
     *
     * @param id identificador da despesa
     * @return DTO de resposta
     * @throws EntityNotFoundException se a despesa não existir
     */
    @Transactional(readOnly = true)
    public DespesaResponseDTO findById(UUID id) throws EntityNotFoundException {
        return despesaMapper.toResponseDTO(
                despesaRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Despesa not found: " + id)));
    }

    /**
     * Lista todas as despesas, paginadas.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<DespesaResponseDTO> findAll(Pageable pageable) {
        return despesaRepository.findAll(pageable)
                .map(despesaMapper::toResponseDTO);
    }

    /**
     * Lista as despesas de uma residência. Valida a residência antes, para que uma
     * residência inexistente devolva 404 e não uma página vazia.
     *
     * @param residenciaId identificador da residência
     * @param pageable     paginação e ordenação
     * @return página de DTOs das despesas
     * @throws EntityNotFoundException se a residência não existir
     */
    @Transactional(readOnly = true)
    public Page<DespesaResponseDTO> findByResidencia(UUID residenciaId, Pageable pageable)
            throws EntityNotFoundException {
        return despesaRepository.findByResidencia_Id(resolveResidencia(residenciaId).getId(), pageable)
                .map(despesaMapper::toResponseDTO);
    }

    /**
     * Cadastra uma despesa e a liga à residência informada.
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta da despesa criada
     * @throws EntityNotFoundException se a residência informada não existir
     */
    @Transactional
    public DespesaResponseDTO create(DespesaCreateDTO createDTO) throws EntityNotFoundException {
        Despesa despesa = despesaMapper.toEntity(createDTO);
        despesa.setResidencia(resolveResidencia(createDTO.residenciaId()));
        Despesa saved = despesaRepository.save(despesa);
        return despesaMapper.toResponseDTO(saved);
    }

    /**
     * Substitui todos os dados da despesa, incluindo a residência.
     *
     * @param id        identificador da despesa
     * @param updateDTO dados de entrada
     * @return DTO de resposta da despesa atualizada
     * @throws EntityNotFoundException se a despesa ou a residência informada não existirem
     */
    @Transactional
    public DespesaResponseDTO update(UUID id, DespesaUpdateDTO updateDTO)
            throws EntityNotFoundException {
        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Despesa not found: " + id));
        despesa.setResidencia(resolveResidencia(updateDTO.residenciaId()));
        despesaMapper.updateEntityFromDTO(updateDTO, despesa);
        Despesa saved = despesaRepository.save(despesa);
        return despesaMapper.toResponseDTO(saved);
    }

    /**
     * Exclui uma despesa isolada.
     *
     * <p>A despesa também morre em cascata quando a residência é removida via
     * {@code ResidenciaService}; este método é o caminho usado quando apenas a
     * despesa está errada e a residência continua de pé.</p>
     *
     * @param id identificador da despesa
     * @throws EntityNotFoundException se a despesa não existir
     */
    @Transactional
    public void delete(UUID id) throws EntityNotFoundException {
        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Despesa not found: " + id));
        despesaRepository.delete(despesa);
    }

    /**
     * Soma as despesas de uma residência.
     *
     * <p>A somatória é a saída que o entrevistador usa para validar se a família
     * pode receber uma cesta: quanto a casa compromete por mês. O valor é somado
     * sem arredondar e só então cortado na escala 2.</p>
     *
     * @param residenciaId identificador da residência
     * @return total das despesas, zero quando não há registros
     * @throws EntityNotFoundException se a residência não existir
     */
    @Transactional(readOnly = true)
    public BigDecimal somaPorResidencia(UUID residenciaId) throws EntityNotFoundException {
        resolveResidencia(residenciaId);
        return despesaRepository.findByResidencia_Id(residenciaId, Pageable.unpaged()).stream()
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.UNNECESSARY);
    }
}