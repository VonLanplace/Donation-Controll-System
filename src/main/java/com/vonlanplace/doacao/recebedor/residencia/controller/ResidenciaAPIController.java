package com.vonlanplace.doacao.recebedor.residencia.controller;

import com.vonlanplace.doacao.recebedor.residencia.ResidenciaCreateDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaService;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * API REST de residências, prefixo {@code /api/residencia}.
 *
 * <p><strong>Os quatro filtros de {@code /search} são mutuamente exclusivos e
 * avaliados nesta ordem:</strong> logradouro, CEP, cidade, bairro. Enviar mais de um não
 * gera erro; o primeiro da lista vence. Sem nenhum filtro, devolve a listagem
 * completa.</p>
 *
 * <p><strong>As buscas são por igualdade exata, não por conter.</strong>
 * {@code findByCidade} gera {@code city = ?}, então "recife" não encontra "Recife" nem
 * "São José do Recife". Não há normalização de caixa nem de acentos em nenhum ponto do
 * caminho.</p>
 */
@RestController
@RequestMapping("/api/residencia")
@Tag(name = "Residencia", description = "Gerenciamento de residências dos recebedores")
public class ResidenciaAPIController {

    private final ResidenciaService residenciaService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param residenciaService serviço de residências
     */
    public ResidenciaAPIController(ResidenciaService residenciaService) {
        this.residenciaService = residenciaService;
    }

    /**
     * Lista todas as residências.
     *
     * @param pageable paginação e ordenação
     * @return página de residências
     */
    @GetMapping
    @Operation(summary = "List all Residences")
    public Page<ResidenciaResponseDTO> getAll(Pageable pageable) {
        return residenciaService.findAll(pageable);
    }

    /**
     * Busca residências por um único critério, na ordem de precedência logradouro, CEP,
     * cidade e bairro.
     *
     * @param logradouro logradouro exato
     * @param cep        CEP exato, sem hífen
     * @param cidade     cidade exata
     * @param bairro     bairro exato
     * @param pageable   paginação e ordenação
     * @return página de residências correspondente ao primeiro critério informado
     */
    @GetMapping("/search")
    @Operation(summary = "Search Residences by street, zip, city or neighborhood")
    public Page<ResidenciaResponseDTO> search(
            @RequestParam(required = false) String logradouro,
            @RequestParam(required = false) String cep,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String bairro,
            Pageable pageable
    ) {
        if (logradouro != null) return residenciaService.findByLogradouro(logradouro, pageable);
        if (cep != null) return residenciaService.findByCep(cep, pageable);
        if (cidade != null) return residenciaService.findByCidade(cidade, pageable);
        if (bairro != null) return residenciaService.findByBairro(bairro, pageable);
        return residenciaService.findAll(pageable);
    }

    /**
     * Cadastra uma residência.
     *
     * @param createDTO dados da nova residência
     * @return a residência criada
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Residence")
    public ResidenciaResponseDTO createResidencia(
            @Valid @RequestBody ResidenciaCreateDTO createDTO
    ) {
        return residenciaService.create(createDTO);
    }

    /**
     * Busca uma residência pelo identificador.
     *
     * @param id identificador da residência
     * @return a residência encontrada
     */
    @GetMapping("/{id}")
    @Operation(summary = "Read a Residence by Id")
    public ResidenciaResponseDTO getResidenciaById(@PathVariable UUID id) {
        return residenciaService.findById(id);
    }

    /**
     * Atualiza uma residência. Todos os campos são substituídos.
     *
     * @param id        identificador da residência
     * @param updateDTO novos dados
     * @return a residência atualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a Residence")
    public ResidenciaResponseDTO updateResidencia(
            @PathVariable UUID id,
            @Valid @RequestBody ResidenciaUpdateDTO updateDTO
    ) {
        return residenciaService.update(id, updateDTO);
    }

    /**
     * Remove uma residência. Retorna 409 se houver moradores vinculados.
     *
     * @param id identificador da residência
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Residence by Id")
    public void deleteResidencia(@PathVariable UUID id) {
        residenciaService.delete(id);
    }
}
