package com.vonlanplace.doacao.produto.marca.controller;

import com.vonlanplace.doacao.produto.marca.MarcaProdutoCreateDTO;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoResponseDTO;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoService;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * API REST de marcas de produto, prefixo {@code /api/marca-produto}.
 *
 * <p><strong>O path variable é o nome, não o id.</strong> Como o nome pode conter
 * espaços e acentos, ele precisa ser percent-encoded na URL — por exemplo
 * {@code /api/marca-produto/Adidas%20do%20Brasil}.</p>
 */
@RestController
@RequestMapping("/api/marca-produto")
@Tag(name = "MarcaProduto", description = "Gerenciamento de marcas de produto")
public class MarcaProdutoAPIController {

    private final MarcaProdutoService marcaProdutoService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param marcaProdutoService serviço de marcas
     */
    public MarcaProdutoAPIController(MarcaProdutoService marcaProdutoService) {
        this.marcaProdutoService = marcaProdutoService;
    }

    /**
     * Lista todas as marcas.
     *
     * @param pageable paginação e ordenação
     * @return página de marcas
     */
    @GetMapping
    @Operation(summary = "List all Product Brands")
    public Page<MarcaProdutoResponseDTO> getAll(Pageable pageable) {
        return marcaProdutoService.findAll(pageable);
    }

    /**
     * Cria uma marca.
     *
     * @param createDTO dados da nova marca
     * @return a marca criada
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Product Brand")
    public MarcaProdutoResponseDTO createMarcaProduto(
            @Valid @RequestBody MarcaProdutoCreateDTO createDTO
    ) {
        return marcaProdutoService.create(createDTO);
    }

    /**
     * Busca a marca pelo nome.
     *
     * @param nome nome da marca
     * @return a marca encontrada
     */
    @GetMapping("/{nome}")
    @Operation(summary = "Read a Product Brand by Name")
    public MarcaProdutoResponseDTO getMarcaProdutoByNome(@PathVariable String nome) {
        return marcaProdutoService.findByNome(nome);
    }

    /**
     * Renomeia a marca identificada pelo nome atual.
     *
     * @param nome      nome atual da marca
     * @param updateDTO dados com o novo nome
     * @return a marca atualizada
     */
    @PutMapping("/{nome}")
    @Operation(summary = "Update a Product Brand by current Name")
    public MarcaProdutoResponseDTO updateMarcaProduto(
            @PathVariable String nome,
            @Valid @RequestBody MarcaProdutoUpdateDTO updateDTO
    ) {
        return marcaProdutoService.update(nome, updateDTO);
    }

    /**
     * Remove a marca pelo nome. Retorna 409 se houver produtos vinculados.
     *
     * @param nome nome da marca
     */
    @DeleteMapping("/{nome}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Product Brand by Name")
    public void deleteMarcaProduto(@PathVariable String nome) {
        marcaProdutoService.delete(nome);
    }
}
