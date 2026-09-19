package com.vonlanplace.doacao.produto.marca;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MarcaProdutoService {

    @Autowired
    private MarcaProdutoRepository marcaProdutoRepository;
    @Autowired
    private MarcaProdutoMapper marcaProdutoMapper;

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
    public List<MarcaProdutoResponseDTO> findAll() {
        return marcaProdutoRepository.findAll()
                .stream()
                .map(marcaProdutoMapper::toResponseDTO)
                .toList();
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
        marcaProdutoMapper.updateEntityFromDTO(updateDTO, marcaProduto);
        MarcaProduto marcaProdutoSaved = marcaProdutoRepository.save(marcaProduto);
        return marcaProdutoMapper.toResponseDTO(marcaProdutoSaved);
    }

    @Transactional
    public void delete(MarcaProduto marcaProduto) {
        marcaProdutoRepository.delete(marcaProduto);
    }
}
