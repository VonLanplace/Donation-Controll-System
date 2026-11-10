package com.vonlanplace.doacao.recebedor.dependente.controller;

import com.vonlanplace.doacao.recebedor.dependente.DependenteCreateDTO;
import com.vonlanplace.doacao.recebedor.dependente.DependenteResponseDTO;
import com.vonlanplace.doacao.recebedor.dependente.DependenteService;
import com.vonlanplace.doacao.recebedor.dependente.DependenteUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API REST de dependentes, prefixo {@code /api/dependente}.
 *
 * <p>Sem filtro, lista todos; com {@code recebedorId}, lista só os
 * dependentes daquele recebedor. As duas respostas são {@link Page}, para a
 * API manter um contrato estável.</p>
 */
@RestController
@RequestMapping("/api/dependente")
@Tag(name = "Dependente", description = "Gerenciamento de dependentes")
public class DependenteAPIController {

    private final DependenteService dependenteService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param dependenteService serviço de dependentes
     */
    public DependenteAPIController(DependenteService dependenteService) {
        this.dependenteService = dependenteService;
    }

    /**
     * Lista dependentes, opcionalmente filtrados por recebedor.
     *
     * @param recebedorId identificador do recebedor, opcional
     * @param pageable    paginação e ordenação
     * @return página de dependentes
     */
    @GetMapping
    @Operation(summary = "List all Dependents")
    public Page<DependenteResponseDTO> getAll(
            @RequestParam(required = false) UUID recebedorId,
            Pageable pageable
    ) {
        if (recebedorId != null) {
            return dependenteService.findByRecebedor(recebedorId, pageable);
        }
        return dependenteService.findAll(pageable);
    }

    /**
     * Cadastra um dependente.
     *
     * @param createDTO dados do novo dependente
     * @return o dependente criado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Dependent")
    public DependenteResponseDTO createDependente(
            @Valid @RequestBody DependenteCreateDTO createDTO
    ) {
        return dependenteService.create(createDTO);
    }

    /**
     * Busca um dependente pelo identificador.
     *
     * @param id identificador do dependente
     * @return o dependente encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Read a Dependent by Id")
    public DependenteResponseDTO getDependenteById(@PathVariable UUID id) {
        return dependenteService.findById(id);
    }

    /**
     * Atualiza um dependente. Todos os campos são substituídos.
     *
     * @param id        identificador do dependente
     * @param updateDTO novos dados
     * @return o dependente atualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a Dependent")
    public DependenteResponseDTO updateDependente(
            @PathVariable UUID id,
            @Valid @RequestBody DependenteUpdateDTO updateDTO
    ) {
        return dependenteService.update(id, updateDTO);
    }

    /**
     * Remove um dependente.
     *
     * @param id identificador do dependente
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Dependent by Id")
    public void deleteDependente(@PathVariable UUID id) {
        dependenteService.delete(id);
    }
}