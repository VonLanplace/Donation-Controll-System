package com.vonlanplace.doacao.produto.controller;

import com.vonlanplace.doacao.produto.ProdutoCreateDTO;
import com.vonlanplace.doacao.produto.ProdutoResponseDTO;
import com.vonlanplace.doacao.produto.ProdutoService;
import com.vonlanplace.doacao.produto.ProdutoUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API REST de produtos.
 *
 * <p>Prefixo {@code /api/produto}. A paginação e a ordenação são resolvidas pelo
 * parâmetro {@link Pageable} do Spring Data; o serviço devolve {@link Page}, que é
 * serializado no formato de coleção paginada padrão do Spring Data.</p>
 *
 * @see ProdutoService
 */
@RestController
@RequestMapping("/api/produto")
@Tag(name = "Produto", description = "Gerenciamento de produtos doáveis")
public class ProdutoAPIController {

    private final ProdutoService produtoService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param produtoService serviço de produtos
     */
    public ProdutoAPIController(
            ProdutoService produtoService
    ) {
        this.produtoService = produtoService;
    }

    /**
     * Lista todos os produtos.
     *
     * @param pageable paginação e ordenação
     * @return página de produtos
     */
    @GetMapping
    @Operation(summary = "List all Products")
    public Page<ProdutoResponseDTO> getAll(Pageable pageable) {
        return produtoService.findAll(pageable);
    }

    /**
     * Busca produtos por um único campo, com os critérios avaliados nesta ordem:
     * código de barras, marca, tipo. Sem nenhum critério, o resultado é a listagem
     * completa.
     *
     * @param codigoBarras  código de barras exato
     * @param marcaProduto  nome da marca
     * @param tipoProduto   nome do tipo
     * @param pageable      paginação e ordenação
     * @return página de produtos correspondente ao primeiro critério informado
     */
    @GetMapping("/search")
    @Operation(summary = "Search Products by barcode, brand or type")
    public Page<ProdutoResponseDTO> search(
            @RequestParam(required = false) String codigoBarras,
            @RequestParam(required = false) String marcaProduto,
            @RequestParam(required = false) String tipoProduto,
            Pageable pageable
    ) {
        if (codigoBarras != null) return produtoService.findByCodigoBarras(codigoBarras, pageable);
        if (marcaProduto != null) return produtoService.findByMarcaProduto(marcaProduto, pageable);
        if (tipoProduto != null) return produtoService.findByTipoProduto(tipoProduto, pageable);
        return produtoService.findAll(pageable);
    }

    /**
     * Cria um produto. Marca e tipo são informados pelo nome.
     *
     * @param produtoCreateDTO dados do novo produto
     * @return o produto criado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Product")
    public ProdutoResponseDTO createProduto(
            @Valid @RequestBody ProdutoCreateDTO produtoCreateDTO
    ) {
        return produtoService.create(produtoCreateDTO);
    }

    /**
     * Busca um produto pelo identificador.
     *
     * @param id identificador do produto
     * @return o produto encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Read a Product by Id")
    public ProdutoResponseDTO getProdutoById(@PathVariable UUID id) {
        return produtoService.findById(id);
    }

    /**
     * Atualiza um produto existente.
     *
     * @param id        identificador do produto
     * @param updateDTO novos dados
     * @return o produto atualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a Product")
    public ProdutoResponseDTO updateProduto(
            @PathVariable UUID id,
            @Valid @RequestBody ProdutoUpdateDTO updateDTO
    ) {
        return produtoService.update(id, updateDTO);
    }

    /**
     * Remove um produto.
     *
     * @param id identificador do produto
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Product by Id")
    public void deleteProduto(
            @PathVariable UUID id
    ) {
        produtoService.delete(id);
    }
}
