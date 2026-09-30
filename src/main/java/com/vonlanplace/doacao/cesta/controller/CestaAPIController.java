package com.vonlanplace.doacao.cesta.controller;

import com.vonlanplace.doacao.cesta.CestaCreateDTO;
import com.vonlanplace.doacao.cesta.CestaResponseDTO;
import com.vonlanplace.doacao.cesta.CestaService;
import com.vonlanplace.doacao.cesta.CestaUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API REST de cestas, prefixo {@code /api/cesta}.
 *
 * <p><strong>Sem endpoint de busca.</strong> Não há filtro por data de entrega nem por
 * recebedor; {@code CestaRepository} não declara consultas além do
 * {@code existsByRecebedor} usado na trava de exclusão do recebedor.</p>
 *
 * <p><strong>Os corpos de criação e atualização podem ter os dois campos nulos.</strong>
 * A validação não impõe nada aqui, então um {@code {}} vazio cria uma cesta sem data e
 * sem recebedor, e um {@code PUT} com {@code recebedorId} ausente desliga o vínculo.</p>
 */
@RestController
@RequestMapping("/api/cesta")
@Tag(name = "Cesta", description = "Gerenciamento de cestas de entrega")
public class CestaAPIController {

    private final CestaService cestaService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param cestaService serviço de cestas
     */
    public CestaAPIController(CestaService cestaService) {
        this.cestaService = cestaService;
    }

    /**
     * Lista todas as cestas.
     *
     * @param pageable paginação e ordenação
     * @return página de cestas
     */
    @GetMapping
    @Operation(summary = "List all Baskets")
    public Page<CestaResponseDTO> getAll(Pageable pageable) {
        return cestaService.findAll(pageable);
    }

    /**
     * Registra uma cesta.
     *
     * @param createDTO dados da nova cesta
     * @return a cesta criada
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Basket")
    public CestaResponseDTO createCesta(
            @Valid @RequestBody CestaCreateDTO createDTO
    ) {
        return cestaService.create(createDTO);
    }

    /**
     * Busca uma cesta pelo identificador.
     *
     * @param id identificador da cesta
     * @return a cesta encontrada
     */
    @GetMapping("/{id}")
    @Operation(summary = "Read a Basket by Id")
    public CestaResponseDTO getCestaById(@PathVariable UUID id) {
        return cestaService.findById(id);
    }

    /**
     * Atualiza uma cesta. Omissão do recebedor desliga o vínculo.
     *
     * @param id        identificador da cesta
     * @param updateDTO novos dados
     * @return a cesta atualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a Basket")
    public CestaResponseDTO updateCesta(
            @PathVariable UUID id,
            @Valid @RequestBody CestaUpdateDTO updateDTO
    ) {
        return cestaService.update(id, updateDTO);
    }

    /**
     * Remove uma cesta.
     *
     * @param id identificador da cesta
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Basket by Id")
    public void deleteCesta(@PathVariable UUID id) {
        cestaService.delete(id);
    }
}
