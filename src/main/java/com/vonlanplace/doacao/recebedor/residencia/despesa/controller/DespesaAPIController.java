package com.vonlanplace.doacao.recebedor.residencia.despesa.controller;

import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaCreateDTO;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaService;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * API REST de despesas, prefixo {@code /api/despesa}.
 *
 * <p>A somatória da residência é o contrato novo: {@code /soma/{residenciaId}}
 * devolve o total mensal, que o entrevistador confronta com o total de
 * auxílios para validar a cesta.</p>
 */
@RestController
@RequestMapping("/api/despesa")
@Tag(name = "Despesa", description = "Gerenciamento de despesas")
public class DespesaAPIController {

    private final DespesaService despesaService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param despesaService serviço de despesas
     */
    public DespesaAPIController(DespesaService despesaService) {
        this.despesaService = despesaService;
    }

    /**
     * Lista despesas, opcionalmente filtradas por residência.
     *
     * @param residenciaId identificador da residência, opcional
     * @param pageable     paginação e ordenação
     * @return página de despesas
     */
    @GetMapping
    @Operation(summary = "List all Expenses")
    public Page<DespesaResponseDTO> getAll(
            @RequestParam(required = false) UUID residenciaId,
            Pageable pageable
    ) {
        if (residenciaId != null) {
            return despesaService.findByResidencia(residenciaId, pageable);
        }
        return despesaService.findAll(pageable);
    }

    /**
     * Soma as despesas de uma residência.
     *
     * @param residenciaId identificador da residência
     * @return total mensal das despesas da residência
     */
    @GetMapping("/soma/{residenciaId}")
    @Operation(summary = "Sum the Expenses of a Residence")
    public BigDecimal soma(@PathVariable UUID residenciaId) {
        return despesaService.somaPorResidencia(residenciaId);
    }

    /**
     * Cadastra uma despesa.
     *
     * @param createDTO dados da nova despesa
     * @return a despesa criada
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an Expense")
    public DespesaResponseDTO createDespesa(
            @Valid @RequestBody DespesaCreateDTO createDTO
    ) {
        return despesaService.create(createDTO);
    }

    /**
     * Busca uma despesa pelo identificador.
     *
     * @param id identificador da despesa
     * @return a despesa encontrada
     */
    @GetMapping("/{id}")
    @Operation(summary = "Read an Expense by Id")
    public DespesaResponseDTO getDespesaById(@PathVariable UUID id) {
        return despesaService.findById(id);
    }

    /**
     * Atualiza uma despesa. Todos os campos são substituídos.
     *
     * @param id        identificador da despesa
     * @param updateDTO novos dados
     * @return a despesa atualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an Expense")
    public DespesaResponseDTO updateDespesa(
            @PathVariable UUID id,
            @Valid @RequestBody DespesaUpdateDTO updateDTO
    ) {
        return despesaService.update(id, updateDTO);
    }

    /**
     * Remove uma despesa.
     *
     * @param id identificador da despesa
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an Expense by Id")
    public void deleteDespesa(@PathVariable UUID id) {
        despesaService.delete(id);
    }
}