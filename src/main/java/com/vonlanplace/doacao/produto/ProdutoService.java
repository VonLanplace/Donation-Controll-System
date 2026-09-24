package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoRepository;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProdutoService {

    final private ProdutoRepository produtoRepository;
    final private ProdutoMapper produtoMapper;
    final private MarcaProdutoRepository marcaProdutoRepository;
    final private TipoProdutoRepository tipoProdutoRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            ProdutoMapper produtoMapper,
            MarcaProdutoRepository marcaProdutoRepository,
            TipoProdutoRepository tipoProdutoRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
        this.marcaProdutoRepository = marcaProdutoRepository;
        this.tipoProdutoRepository = tipoProdutoRepository;
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO findById(UUID id) throws EntityNotFoundException {
        return produtoMapper.toResponseDTO(
                produtoRepository.findById(id).orElseThrow(
                        EntityNotFoundException::new));
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> findByCodigoBarras(
            String codigoBarras,
            Pageable pageable
    ) throws EntityNotFoundException {
        return produtoRepository.findByCodigoBarras(codigoBarras, pageable)
                .map(produtoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> findByMarcaProduto(
            UUID marcaProdutoId,
            Pageable pageable
    ) throws EntityNotFoundException {
        MarcaProduto marcaProduto = marcaProdutoRepository.findById(marcaProdutoId)
                .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found"));
        return produtoRepository.findAllByMarcaProduto(marcaProduto, pageable)
                .map(produtoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> findByTipoProduto(
            UUID tipoProdutoId,
            Pageable pageable
    ) throws EntityNotFoundException {
        TipoProduto tipoProduto = tipoProdutoRepository.findById(tipoProdutoId)
                .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found"));
        return produtoRepository.findAllByTipoProduto(tipoProduto, pageable)
                .map(produtoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> findAll(Pageable pageable) throws EntityNotFoundException {
        return produtoRepository.findAll(pageable)
                .map(produtoMapper::toResponseDTO);
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
    public ProdutoResponseDTO update(UUID id, ProdutoUpdateDTO updateDTO) throws EntityNotFoundException {
        Produto produto = produtoRepository.findById(id)
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
