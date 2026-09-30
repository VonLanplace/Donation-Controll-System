package com.vonlanplace.doacao.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Regras de negócio de {@link Cesta}.
 *
 * <p><strong>CRUD simples, com vínculo opcional.</strong> A diferença em relação aos
 * outros serviços está em {@link #resolveRecebedor(UUID)}: id {@code null} não é erro,
 * devolve {@code null} e a cesta fica sem recebedor. Id preenchido e inexistente é 404.
 * O mesmo tratamento vale para {@code DoacaoService}.</p>
 *
 * <p>Não há guarda de exclusão aqui — a cesta não é referenciada por nada, então pode
 * sair a qualquer momento. A trava que existe é a oposta, em
 * {@code RecebedorService.delete}, que recusa remover um recebedor com cestas
 * vinculadas.</p>
 */
@Service
public class CestaService {

    final private CestaRepository cestaRepository;
    final private CestaMapper cestaMapper;
    final private RecebedorRepository recebedorRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param cestaRepository      repositório de cestas
     * @param cestaMapper          conversor entidade/DTO
     * @param recebedorRepository  repositório de recebedores, usado para resolver o
     *                            vínculo informado pelo id
     */
    public CestaService(
            CestaRepository cestaRepository,
            CestaMapper cestaMapper,
            RecebedorRepository recebedorRepository
    ) {
        this.cestaRepository = cestaRepository;
        this.cestaMapper = cestaMapper;
        this.recebedorRepository = recebedorRepository;
    }

    /**
     * Resolve o recebedor, tolerando ausência de vínculo.
     *
     * @param recebedorId identificador informado, pode ser {@code null}
     * @return o recebedor, ou {@code null} se o id for {@code null}
     * @throws EntityNotFoundException se o id for preenchido e não existir
     */
    private Recebedor resolveRecebedor(UUID recebedorId) {
        if (recebedorId == null) return null;
        return recebedorRepository.findById(recebedorId)
                .orElseThrow(() -> new EntityNotFoundException("Recebedor not found: " + recebedorId));
    }

    /**
     * Busca a cesta pelo identificador.
     *
     * @param id identificador da cesta
     * @return DTO de resposta
     * @throws EntityNotFoundException se a cesta não existir
     */
    @Transactional(readOnly = true)
    public CestaResponseDTO findById(UUID id) throws EntityNotFoundException {
        return cestaMapper.toResponseDTO(
                cestaRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Cesta not found: " + id)));
    }

    /**
     * Lista todas as cestas, paginadas.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<CestaResponseDTO> findAll(Pageable pageable) {
        return cestaRepository.findAll(pageable)
                .map(cestaMapper::toResponseDTO);
    }

    /**
     * Registra uma cesta, com recebedor opcional.
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta da cesta criada
     * @throws EntityNotFoundException se o recebedor informado não existir
     */
    @Transactional
    public CestaResponseDTO create(CestaCreateDTO createDTO) throws EntityNotFoundException {
        Cesta cesta = cestaMapper.toEntity(createDTO);
        cesta.setRecebedor(resolveRecebedor(createDTO.recebedorId()));
        Cesta saved = cestaRepository.save(cesta);
        return cestaMapper.toResponseDTO(saved);
    }

    /**
     * Atualiza a cesta. Omissão de {@code recebedorId} desliga o vínculo; data de
     * entrega omitida mantém o valor anterior.
     *
     * @param id        identificador da cesta
     * @param updateDTO dados de entrada
     * @return DTO de resposta da cesta atualizada
     * @throws EntityNotFoundException se a cesta ou o recebedor informado não existirem
     */
    @Transactional
    public CestaResponseDTO update(UUID id, CestaUpdateDTO updateDTO) throws EntityNotFoundException {
        Cesta cesta = cestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cesta not found: " + id));
        cesta.setRecebedor(resolveRecebedor(updateDTO.recebedorId()));
        cestaMapper.updateEntityFromDTO(updateDTO, cesta);
        Cesta saved = cestaRepository.save(cesta);
        return cestaMapper.toResponseDTO(saved);
    }

    /**
     * Exclui a cesta. Nada referencia a cesta, então a remoção é sempre livre.
     *
     * @param id identificador da cesta
     * @throws EntityNotFoundException se a cesta não existir
     */
    @Transactional
    public void delete(UUID id) throws EntityNotFoundException {
        Cesta cesta = cestaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cesta not found: " + id));
        cestaRepository.delete(cesta);
    }
}
