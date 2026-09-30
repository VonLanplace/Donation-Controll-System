package com.vonlanplace.doacao.recebedor.auxilio.controller;

import com.vonlanplace.doacao.recebedor.auxilio.AuxilioCreateDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioResponseDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioService;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioUpdateDTO;
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
 * API REST de auxílios, prefixo {@code /api/auxilio}.
 *
 * <p>A somatória do recebedor é o contrato novo: {@code /soma/{recebedorId}}
 * devolve o total mensal de renda assistencial, que o entrevistador confronta
 * com as despesas da residência para validar a cesta.</p>
 */
@RestController
@RequestMapping("/api/auxilio")
@Tag(name = "Auxilio", description = "Gerenciamento de auxílios")
public class AuxilioAPIController {

    private final AuxilioService auxilioService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param auxilioService serviço de auxílios
     */
    public AuxilioAPIController(AuxilioService auxilioService) {
        this.auxilioService = auxilioService;
    }

    /**
     * Lista auxílios, opcionalmente filtrados por recebedor.
     *
     * @param recebedorId identificador do recebedor, opcional
     * @param pageable    paginação e ordenação
     * @return página de auxílios
     */
    @GetMapping
    @Operation(summary = "List all Assistances")
    public Page<AuxilioResponseDTO> getAll(
            @RequestParam(required = false) UUID recebedorId,
            Pageable pageable
    ) {
        if (recebedorId != null) {
            return auxilioService.findByRecebedor(recebedorId, pageable);
        }
        return auxilioService.findAll(pageable);
    }

    /**
     * Soma os auxílios de um recebedor.
     *
     * @param recebedorId identificador do recebedor
     * @return total mensal de renda assistencial do recebedor
     */
    @GetMapping("/soma/{recebedorId}")
    @Operation(summary = "Sum the Assistances of a Recipient")
    public BigDecimal soma(@PathVariable UUID recebedorId) {
        return auxilioService.somaPorRecebedor(recebedorId);
    }

    /**
     * Cadastra um auxílio.
     *
     * @param createDTO dados do novo auxílio
     * @return o auxílio criado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an Assistance")
    public AuxilioResponseDTO createAuxilio(
            @Valid @RequestBody AuxilioCreateDTO createDTO
    ) {
        return auxilioService.create(createDTO);
    }

    /**
     * Busca um auxílio pelo identificador.
     *
     * @param id identificador do auxílio
     * @return o auxílio encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Read an Assistance by Id")
    public AuxilioResponseDTO getAuxilioById(@PathVariable UUID id) {
        return auxilioService.findById(id);
    }

    /**
     * Atualiza um auxílio. Todos os campos são substituídos.
     *
     * @param id        identificador do auxílio
     * @param updateDTO novos dados
     * @return o auxílio atualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an Assistance")
    public AuxilioResponseDTO updateAuxilio(
            @PathVariable UUID id,
            @Valid @RequestBody AuxilioUpdateDTO updateDTO
    ) {
        return auxilioService.update(id, updateDTO);
    }

    /**
     * Remove um auxílio.
     *
     * @param id identificador do auxílio
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an Assistance by Id")
    public void deleteAuxilio(@PathVariable UUID id) {
        auxilioService.delete(id);
    }
}