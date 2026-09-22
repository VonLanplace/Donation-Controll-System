package com.vonlanplace.doacao.produto.marca;

import com.vonlanplace.doacao.produto.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MarcaProdutoService {

    final private MarcaProdutoRepository marcaProdutoRepository;
    final private MarcaProdutoMapper marcaProdutoMapper;
    final private ProdutoRepository produtoRepository;

    public MarcaProdutoService(
            MarcaProdutoRepository marcaProdutoRepository,
            MarcaProdutoMapper marcaProdutoMapper,
            ProdutoRepository produtoRepository
    ) {
        this.marcaProdutoRepository = marcaProdutoRepository;
        this.marcaProdutoMapper = marcaProdutoMapper;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public MarcaProdutoResponseDTO findById(UUID id) throws EntityNotFoundException {
        MarcaProduto marcaProduto = marcaProdutoRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return marcaProdutoMapper.toResponseDTO(marcaProduto);
    }

    @Transactional(readOnly = true)
    public MarcaProdutoResponseDTO findByNome(String nome) throws EntityNotFoundException {
        MarcaProduto marcaProduto = marcaProdutoRepository.findByNome(nome)
                .orElseThrow(EntityNotFoundException::new);
        return marcaProdutoMapper.toResponseDTO(marcaProduto);
    }

    @Transactional(readOnly = true)
    public Page<MarcaProdutoResponseDTO> findAll(Pageable pageable) {
        return marcaProdutoRepository.findAll(pageable)
                .map(marcaProdutoMapper::toResponseDTO);
    }

    @Transactional
    public MarcaProdutoResponseDTO create(MarcaProdutoCreateDTO createDTO) {
        MarcaProduto marcaProduto = marcaProdutoMapper.toEntity(createDTO);
        MarcaProduto marcaProdutoSaved = marcaProdutoRepository.save(marcaProduto);
        return marcaProdutoMapper.toResponseDTO(marcaProdutoSaved);
    }

    @Transactional
    public MarcaProdutoResponseDTO update(MarcaProdutoUpdateDTO updateDTO) throws EntityNotFoundException {
        MarcaProduto marcaProduto = marcaProdutoRepository.findById(updateDTO.id())
                .orElseThrow(EntityNotFoundException::new);
        marcaProdutoRepository.findByNome(updateDTO.nome())
                .filter(existing -> !existing.getId().equals(updateDTO.id()))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Nome já em uso por outra marca");
                });

        marcaProdutoMapper.updateEntityFromDTO(updateDTO, marcaProduto);
        MarcaProduto marcaProdutoSaved = marcaProdutoRepository.save(marcaProduto);
        return marcaProdutoMapper.toResponseDTO(marcaProdutoSaved);
    }

    @Transactional
    public void delete(UUID marcaProdutoId) throws EntityNotFoundException {
        MarcaProduto marcaProduto = marcaProdutoRepository.findById(marcaProdutoId)
                .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found"));

        if (!produtoRepository.existsByMarcaProduto(marcaProduto)) {
            throw new IllegalStateException("Não é possível excluir marca com produtos vinculados");
        }

        marcaProdutoRepository.delete(marcaProduto);
    }
}
