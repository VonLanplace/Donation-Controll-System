package com.vonlanplace.doacao.produto.tipo;

import com.vonlanplace.doacao.produto.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TipoProdutoService {

    private TipoProdutoRepository tipoProdutoRepository;
    private TipoProdutoMapper tipoProdutoMapper;
    private ProdutoRepository produtoRepository;

    TipoProdutoService(
            TipoProdutoRepository tipoProdutoRepository,
            TipoProdutoMapper tipoProdutoMapper,
            ProdutoRepository produtoRepository
    ) {
        this.tipoProdutoRepository = tipoProdutoRepository;
        this.tipoProdutoMapper = tipoProdutoMapper;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public TipoProdutoResponseDTO findById(UUID id) throws EntityNotFoundException {
        return tipoProdutoMapper.toResponseDTO(
                tipoProdutoRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException()));
    }

    @Transactional(readOnly = true)
    public TipoProdutoResponseDTO findByNome(String nome) throws EntityNotFoundException {
        return tipoProdutoMapper.toResponseDTO(
                tipoProdutoRepository.findByNome(nome).orElseThrow(
                        () -> new EntityNotFoundException()));
    }

    @Transactional(readOnly = true)
    public List<TipoProdutoResponseDTO> findAll() {
        return tipoProdutoRepository.findAll()
                .stream()
                .map(tipoProdutoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public TipoProdutoResponseDTO create(TipoProdutoCreateDTO createDTO) {
        TipoProduto tipoProdutoFromDto = tipoProdutoMapper.toEntity(createDTO);
        TipoProduto tipoProdutoSaved = tipoProdutoRepository.save(tipoProdutoFromDto);
        return tipoProdutoMapper.toResponseDTO(tipoProdutoSaved);
    }

    @Transactional
    public TipoProdutoResponseDTO update(TipoProdutoUpdateDTO updateDTO) throws EntityNotFoundException {
        TipoProduto tipoProduto = tipoProdutoRepository.findById(updateDTO.id())
                .orElseThrow(() -> new EntityNotFoundException());
        tipoProdutoRepository.findByNome(updateDTO.nome())
                .filter(existing -> !existing.getId().equals(updateDTO.id()))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Nome já em uso por outra marca");
                });

        tipoProdutoMapper.updateEntityFromDTO(updateDTO, tipoProduto);
        TipoProduto tipoProdutoSaved = tipoProdutoRepository.save(tipoProduto);
        return tipoProdutoMapper.toResponseDTO(tipoProdutoSaved);
    }

    @Transactional
    public void delete(UUID tipoProdutoId) throws EntityNotFoundException {
        TipoProduto tipoProduto = tipoProdutoRepository.findById(tipoProdutoId)
                .orElseThrow(() -> new EntityNotFoundException("Tipo Produto not found"));

        if (!produtoRepository.existsByTipoProduto(tipoProduto)) {
            throw new IllegalStateException("Não é possível excluir tipo com produtos vinculados");
        }

        tipoProdutoRepository.delete(tipoProduto);
    }
}
