package com.vonlanplace.doacao.doacao.controller;

import com.vonlanplace.doacao.doacao.DoacaoCreateDTO;
import com.vonlanplace.doacao.doacao.DoacaoResponseDTO;
import com.vonlanplace.doacao.doacao.DoacaoService;
import com.vonlanplace.doacao.doacao.DoacaoUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API REST de doações, prefixo {@code /api/doacao}.
 *
 * <p><strong>Sem endpoint de busca.</strong> A doação não tem filtro próprio aqui — nem
 * por data, nem por recebedor, nem por entrevistador. A listagem aceita paginação e
 * ordenação, e qualquer filtro além disso exigiria consultas no
 * {@code DoacaoRepository}, que hoje só tem o {@code existsByRecebedor} usado na trava
 * de exclusão do recebedor.</p>
 *
 * <p><strong>{@code PUT} substitui a lista de produtos inteira.</strong> Ver
 * {@code DoacaoService.replaceProdutos}: os itens atuais são apagados e os que vierem no
 * corpo são inseridos. Enviar lista vazia remove todos os itens.</p>
 */
@RestController
@RequestMapping("/api/doacao")
@Tag(name = "Doacao", description = "Gerenciamento de doações")
public class DoacaoAPIController {

    private final DoacaoService doacaoService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param doacaoService serviço de doações
     */
    public DoacaoAPIController(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    /**
     * Lista todas as doações.
     *
     * @param pageable paginação e ordenação
     * @return página de doações
     */
    @GetMapping
    @Operation(summary = "List all Donations")
    public Page<DoacaoResponseDTO> getAll(Pageable pageable) {
        return doacaoService.findAll(pageable);
    }

    /**
     * Registra uma doação.
     *
     * @param createDTO dados da nova doação, com a lista de itens
     * @return a doação criada
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Donation")
    public DoacaoResponseDTO createDoacao(
            @Valid @RequestBody DoacaoCreateDTO createDTO
    ) {
        return doacaoService.create(createDTO);
    }

    /**
     * Busca uma doação pelo identificador.
     *
     * @param id identificador da doação
     * @return a doação encontrada
     */
    @GetMapping("/{id}")
    @Operation(summary = "Read a Donation by Id")
    public DoacaoResponseDTO getDoacaoById(@PathVariable UUID id) {
        return doacaoService.findById(id);
    }

    /**
     * Atualiza uma doação, substituindo a lista inteira de produtos.
     *
     * @param id        identificador da doação
     * @param updateDTO novos dados, com a lista final de itens
     * @return a doação atualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a Donation, replacing its product lines")
    public DoacaoResponseDTO updateDoacao(
            @PathVariable UUID id,
            @Valid @RequestBody DoacaoUpdateDTO updateDTO
    ) {
        return doacaoService.update(id, updateDTO);
    }

    /**
     * Remove uma doação e todos os seus itens.
     *
     * @param id identificador da doação
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Donation by Id")
    public void deleteDoacao(@PathVariable UUID id) {
        doacaoService.delete(id);
    }
}
