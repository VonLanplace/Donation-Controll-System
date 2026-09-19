package com.vonlanplace.doacao.produto.tipo;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TipoProdutoService {

    @Autowired
    private TipoProdutoRepository tipoProdutoRepository;
    @Autowired
    private TipoProdutoMapper tipoProdutoMapper;

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
        tipoProdutoMapper.updateEntityFromDTO(updateDTO, tipoProduto);
        TipoProduto tipoProdutoSaved = tipoProdutoRepository.save(tipoProduto);
        return tipoProdutoMapper.toResponseDTO(tipoProdutoSaved);
    }

    @Transactional
    public void delete(TipoProduto tipoProduto) {
        tipoProdutoRepository.delete(tipoProduto);
    }
}
