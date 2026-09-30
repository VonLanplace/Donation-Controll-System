package com.vonlanplace.doacao.recebedor.auxilio;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Regras de negócio de {@link Auxilio}.
 *
 * <p>CRUD simples de sub-registro: criar, editar, listar e somar os auxílios de
 * um recebedor. A soma é a renda assistencial que o entrevistador confronta com
 * as despesas da residência para validar a cesta.</p>
 *
 * <p><strong>A exclusão em cascata vem do pai.</strong> O auxílio morre com o
 * recebedor; {@code delete(UUID)} existe para remover só o auxílio quando o
 * recebedor continua de pé.</p>
 */
@Service
public class AuxilioService {

    final private AuxilioRepository auxilioRepository;
    final private AuxilioMapper auxilioMapper;
    final private RecebedorRepository recebedorRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param auxilioRepository   repositório de auxílios
     * @param auxilioMapper       conversor entidade/DTO
     * @param recebedorRepository repositório de recebedores, para resolver o vínculo
     */
    public AuxilioService(
            AuxilioRepository auxilioRepository,
            AuxilioMapper auxilioMapper,
            RecebedorRepository recebedorRepository
    ) {
        this.auxilioRepository = auxilioRepository;
        this.auxilioMapper = auxilioMapper;
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
     * Busca o auxílio pelo identificador.
     *
     * @param id identificador do auxílio
     * @return DTO de resposta
     * @throws EntityNotFoundException se o auxílio não existir
     */
    @Transactional(readOnly = true)
    public AuxilioResponseDTO findById(UUID id) throws EntityNotFoundException {
        return auxilioMapper.toResponseDTO(
                auxilioRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Auxilio not found: " + id)));
    }

    /**
     * Lista todos os auxílios, paginados.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<AuxilioResponseDTO> findAll(Pageable pageable) {
        return auxilioRepository.findAll(pageable)
                .map(auxilioMapper::toResponseDTO);
    }

    /**
     * Lista os auxílios de um recebedor. Valida o recebedor antes, para que um
     * recebedor inexistente devolva 404 e não uma página vazia.
     *
     * @param recebedorId identificador do recebedor
     * @param pageable    paginação e ordenação
     * @return página de DTOs dos auxílios
     * @throws EntityNotFoundException se o recebedor não existir
     */
    @Transactional(readOnly = true)
    public Page<AuxilioResponseDTO> findByRecebedor(UUID recebedorId, Pageable pageable)
            throws EntityNotFoundException {
        return auxilioRepository.findByRecebedor_Id(resolveRecebedor(recebedorId).getId(), pageable)
                .map(auxilioMapper::toResponseDTO);
    }

    /**
     * Cadastra um auxílio e o liga ao recebedor informado.
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta do auxílio criado
     * @throws EntityNotFoundException se o recebedor informado não existir
     */
    @Transactional
    public AuxilioResponseDTO create(AuxilioCreateDTO createDTO) throws EntityNotFoundException {
        Auxilio auxilio = auxilioMapper.toEntity(createDTO);
        auxilio.setRecebedor(resolveRecebedor(createDTO.recebedorId()));
        Auxilio saved = auxilioRepository.save(auxilio);
        return auxilioMapper.toResponseDTO(saved);
    }

    /**
     * Substitui todos os dados do auxílio, incluindo o recebedor.
     *
     * @param id        identificador do auxílio
     * @param updateDTO dados de entrada
     * @return DTO de resposta do auxílio atualizado
     * @throws EntityNotFoundException se o auxílio ou o recebedor informado não existirem
     */
    @Transactional
    public AuxilioResponseDTO update(UUID id, AuxilioUpdateDTO updateDTO)
            throws EntityNotFoundException {
        Auxilio auxilio = auxilioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Auxilio not found: " + id));
        auxilio.setRecebedor(resolveRecebedor(updateDTO.recebedorId()));
        auxilioMapper.updateEntityFromDTO(updateDTO, auxilio);
        Auxilio saved = auxilioRepository.save(auxilio);
        return auxilioMapper.toResponseDTO(saved);
    }

    /**
     * Exclui um auxílio isolado.
     *
     * <p>O auxílio também morre em cascata quando o recebedor é removido via
     * {@code RecebedorService}; este método é o caminho usado quando apenas o
     * auxílio está errado e o recebedor continua de pé.</p>
     *
     * @param id identificador do auxílio
     * @throws EntityNotFoundException se o auxílio não existir
     */
    @Transactional
    public void delete(UUID id) throws EntityNotFoundException {
        Auxilio auxilio = auxilioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Auxilio not found: " + id));
        auxilioRepository.delete(auxilio);
    }

    /**
     * Soma os auxílios de um recebedor.
     *
     * <p>É a renda assistencial que o entrevistador confronta com as despesas da
     * residência para validar se a família pode receber uma cesta.</p>
     *
     * @param recebedorId identificador do recebedor
     * @return total dos auxílios, zero quando não há registros
     * @throws EntityNotFoundException se o recebedor não existir
     */
    @Transactional(readOnly = true)
    public BigDecimal somaPorRecebedor(UUID recebedorId) throws EntityNotFoundException {
        resolveRecebedor(recebedorId);
        return auxilioRepository.findByRecebedor_Id(recebedorId, Pageable.unpaged()).stream()
                .map(Auxilio::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.UNNECESSARY);
    }
}