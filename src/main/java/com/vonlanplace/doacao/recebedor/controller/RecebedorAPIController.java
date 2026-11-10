package com.vonlanplace.doacao.recebedor.controller;

import com.vonlanplace.doacao.recebedor.RecebedorCreateDTO;
import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;
import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.recebedor.RecebedorUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * API REST de recebedores, prefixo {@code /api/recebedor}.
 *
 * <p><strong>Os cinco filtros de {@code /search} são mutuamente exclusivos e avaliados
 * nesta ordem:</strong> nome, data de nascimento, residência, CPF, PIX. Enviar mais de
 * um não gera erro — o primeiro na lista vence e os demais são silenciosamente
 * ignorados. Sem nenhum filtro, a busca devolve a listagem completa.</p>
 *
 * <p><strong>CPF e PIX devolvem {@code Page} por convenção, não por paginação real.</strong>
 * Ambos são chaves únicas, então o serviço devolve um único registro; o controller o
 * embrulha em {@link PageImpl} para manter o formato de resposta igual ao dos outros
 * filtros. A consequência é que a resposta tem {@code unpaged = true} e tamanho 1
 * independentemente dos parâmetros de paginação enviados.</p>
 */
@RestController
@RequestMapping("/api/recebedor")
@Tag(name = "Recebedor", description = "Gerenciamento de recebedores")
public class RecebedorAPIController {

    private final RecebedorService recebedorService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param recebedorService serviço de recebedores
     */
    public RecebedorAPIController(RecebedorService recebedorService) {
        this.recebedorService = recebedorService;
    }

    /**
     * Lista todos os recebedores.
     *
     * @param pageable paginação e ordenação
     * @return página de recebedores
     */
    @GetMapping
    @Operation(summary = "List all Recipients")
    public Page<RecebedorResponseDTO> getAll(Pageable pageable) {
        return recebedorService.findAll(pageable);
    }

    /**
     * Busca recebedores por um único critério, na ordem de precedência nome, data de
     * nascimento, residência, CPF e PIX.
     *
     * @param nomeCompleto   nome completo exato
     * @param dataNascimento data de nascimento exata, em formato ISO {@code yyyy-MM-dd}
     * @param residenciaId   identificador da residência
     * @param cpf            CPF exato
     * @param pix            chave PIX exata
     * @param pageable       paginação e ordenação
     * @return página de recebedores correspondente ao primeiro critério informado
     */
    @GetMapping("/search")
    @Operation(summary = "Search Recipients by name, birth date, residence, cpf or pix")
    public Page<RecebedorResponseDTO> search(
            @RequestParam(required = false) String nomeCompleto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascimento,
            @RequestParam(required = false) UUID residenciaId,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String pix,
            Pageable pageable
    ) {
        if (nomeCompleto != null) return recebedorService.findByNomeCompleto(nomeCompleto, pageable);
        if (dataNascimento != null) return recebedorService.findByDataNascimento(dataNascimento, pageable);
        if (residenciaId != null) return recebedorService.findByResidencia(residenciaId, pageable);
        if (cpf != null) return new PageImpl<>(List.of(recebedorService.findByCpf(cpf)));
        if (pix != null) return new PageImpl<>(List.of(recebedorService.findByPix(pix)));
        return recebedorService.findAll(pageable);
    }

    /**
     * Cadastra um recebedor.
     *
     * @param createDTO dados do novo recebedor
     * @return o recebedor criado
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Recipient")
    public RecebedorResponseDTO createRecebedor(
            @Valid @RequestBody RecebedorCreateDTO createDTO
    ) {
        return recebedorService.create(createDTO);
    }

    /**
     * Busca um recebedor pelo identificador.
     *
     * @param id identificador do recebedor
     * @return o recebedor encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Read a Recipient by Id")
    public RecebedorResponseDTO getRecebedorById(@PathVariable UUID id) {
        return recebedorService.findById(id);
    }

    /**
     * Atualiza um recebedor. Todos os campos são substituídos.
     *
     * @param id        identificador do recebedor
     * @param updateDTO novos dados
     * @return o recebedor atualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a Recipient")
    public RecebedorResponseDTO updateRecebedor(
            @PathVariable UUID id,
            @Valid @RequestBody RecebedorUpdateDTO updateDTO
    ) {
        return recebedorService.update(id, updateDTO);
    }

    /**
     * Remove um recebedor. Retorna 409 se houver doações ou cestas vinculadas.
     *
     * @param id identificador do recebedor
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Recipient by Id")
    public void deleteRecebedor(@PathVariable UUID id) {
        recebedorService.delete(id);
    }
}
