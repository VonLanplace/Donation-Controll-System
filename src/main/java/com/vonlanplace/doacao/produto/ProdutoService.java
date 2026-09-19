package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoRepository;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ProdutoMapper produtoMapper;

    @Autowired
    private MarcaProdutoRepository marcaProdutoRepository;
    @Autowired
    private TipoProdutoRepository tipoProdutoRepository;

    @Transactional(readOnly = true)
    public ProdutoResponseDTO findById(UUID id) throws EntityNotFoundException {
        return produtoMapper.toResponseDTO(
                produtoRepository.findById(id).orElseThrow(
                        EntityNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO findByCodigoBarras(String codigoBarras) throws EntityNotFoundException {
        return produtoMapper.toResponseDTO(
                produtoRepository.findByCodigoBarras(codigoBarras).orElseThrow(
                        EntityNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findByMarcaProduto(UUID marcaProdutoId) throws EntityNotFoundException {
        MarcaProduto marcaProduto = marcaProdutoRepository.findById(marcaProdutoId)
                .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found"));
        return produtoRepository.findAllByMarcaProduto(marcaProduto)
                .stream()
                .map(produtoMapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findByTipoProduto(UUID tipoProdutoId) throws EntityNotFoundException {
        TipoProduto tipoProduto = tipoProdutoRepository.findById(tipoProdutoId)
                .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found"));
        return produtoRepository.findAllByTipoProduto(tipoProduto)
                .stream()
                .map(produtoMapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findAll() {
        return produtoRepository.findAll()
                .stream()
                .map(produtoMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProdutoResponseDTO create(ProdutoCreateDTO createDTO) throws EntityNotFoundException {
        Produto produto = produtoMapper.toEntity(createDTO);
        produto.setMarcaProduto(
                marcaProdutoRepository.findById(createDTO.marcaProdutoId())
                        .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found")));
        produto.setTipoProduto(
                tipoProdutoRepository.findById(createDTO.tipoProdutoId())
                        .orElseThrow(() -> new EntityNotFoundException("Tipo Produto not found")));
        Produto produtoSaved = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSaved);
    }

    @Transactional
    public ProdutoResponseDTO update(ProdutoUpdateDTO updateDTO) throws EntityNotFoundException {
        Produto produto = produtoRepository.findById(updateDTO.id())
                .orElseThrow(EntityNotFoundException::new);
        produto.setMarcaProduto(
                marcaProdutoRepository.findById(updateDTO.marcaProdutoId())
                        .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found")));
        produto.setTipoProduto(
                tipoProdutoRepository.findById(updateDTO.tipoProdutoId())
                        .orElseThrow(() -> new EntityNotFoundException("Tipo Produto not found")));
        produtoMapper.updateEntityFromDTO(updateDTO, produto);
        Produto produtoSaved = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSaved);
    }

    @Transactional
    public void delete(UUID produtoId) throws EntityNotFoundException {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto not found"));
        produtoRepository.delete(produto);
    }
}
