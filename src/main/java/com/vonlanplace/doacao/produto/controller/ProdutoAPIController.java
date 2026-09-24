package com.vonlanplace.doacao.produto.controller;

import com.vonlanplace.doacao.produto.ProdutoCreateDTO;
import com.vonlanplace.doacao.produto.ProdutoResponseDTO;
import com.vonlanplace.doacao.produto.ProdutoService;
import com.vonlanplace.doacao.produto.ProdutoUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/produto")
public class ProdutoAPIController {

    private final ProdutoService produtoService;

    public ProdutoAPIController(
            ProdutoService produtoService
    ) {
        this.produtoService = produtoService;
    }

    // Get all Produtos items (GET)
    @GetMapping
    public Page<ProdutoResponseDTO> getAll(Pageable pageable) {
        return produtoService.findAll(pageable);
    }

    // Create a Produto (POST)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResponseDTO createProduto(
            @Valid @RequestBody ProdutoCreateDTO produtoCreateDTO
    ) {
        return produtoService.create(produtoCreateDTO);
    }

    // Read Produto by Id (GET)
    @GetMapping("/{id}")
    public ProdutoResponseDTO getProdutoById(@PathVariable UUID id) {
        return produtoService.findById(id);
    }

    // Update Produto (PUT)
    @PutMapping("/{id}")
    public ProdutoResponseDTO updateProduto(
            @PathVariable UUID id,
            @Valid @RequestBody ProdutoUpdateDTO updateDTO
    ) {
        return produtoService.update(id, updateDTO);
    }

    // Remove Produto by Id (DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduto(
            @PathVariable UUID id
    ) {
        produtoService.delete(id);
    }
}
