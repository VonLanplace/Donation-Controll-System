package com.vonlanplace.doacao.produto.tipo.controller;

import com.vonlanplace.doacao.produto.tipo.TipoProdutoCreateDTO;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoResponseDTO;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoService;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * API REST de tipos de produto, prefixo {@code /api/tipo-produto}.
 *
 * <p><strong>O path variable é o nome, não o id.</strong> Nomes com espaços ou acentos
 * precisam ser percent-encoded na URL.</p>
 */
@RestController
@RequestMapping("/api/tipo-produto")
@Tag(name = "TipoProduto", description = "Gerenciamento de tipos de produto")
public class TipoProdutoAPIController {

    private final TipoProdutoService tipoProdutoService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param tipoProdutoService serviço de tipos de produto
     */
    public TipoProdutoAPIController(TipoProdutoService tipoProdutoService) {
        this.tipoProdutoService = tipoProdutoService;
    }

    /**
     * Lista todos os tipos.
     *
     * @param pageable paginação e ordenação
     * @return página de tipos
     */
    @GetMapping
    @Operation(summary = "List all Product Types")
    public Page<TipoProdutoResponseDTO> getAll(Pageable pageable) {
        return tipoProdutoService.findAll(pageable);
    }

    /**
     * Cria um tipo.
     *
     * @param createDTO dados do novo tipo
     * @return o tipo criado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Product Type")
    public TipoProdutoResponseDTO createTipoProduto(
            @Valid @RequestBody TipoProdutoCreateDTO createDTO
    ) {
        return tipoProdutoService.create(createDTO);
    }

    /**
     * Busca o tipo pelo nome.
     *
     * @param nome nome do tipo
     * @return o tipo encontrado
     */
    @GetMapping("/{nome}")
    @Operation(summary = "Read a Product Type by Name")
    public TipoProdutoResponseDTO getTipoProdutoByNome(@PathVariable String nome) {
        return tipoProdutoService.findByNome(nome);
    }

    /**
     * Renomeia o tipo identificado pelo nome atual.
     *
     * @param nome      nome atual do tipo
     * @param updateDTO dados com o novo nome
     * @return o tipo atualizado
     */
    @PutMapping("/{nome}")
    @Operation(summary = "Update a Product Type by current Name")
    public TipoProdutoResponseDTO updateTipoProduto(
            @PathVariable String nome,
            @Valid @RequestBody TipoProdutoUpdateDTO updateDTO
    ) {
        return tipoProdutoService.update(nome, updateDTO);
    }

    /**
     * Remove o tipo pelo nome. Retorna 409 se houver produtos vinculados.
     *
     * @param nome nome do tipo
     */
    @DeleteMapping("/{nome}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Product Type by Name")
    public void deleteTipoProduto(@PathVariable String nome) {
        tipoProdutoService.delete(nome);
    }
}
