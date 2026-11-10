package com.vonlanplace.doacao.recebedor.controller;

import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;
import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.recebedor.cesta.ElegibilidadeCestaDTO;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaCreateDTO;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaResponseDTO;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

/**
 * API REST do histórico de cestas, prefixo {@code /api/recebimento-cesta}.
 *
 * <p>Concentra a elegibilidade, o histórico e o registro mensal. O registro é
 * idempotente por recebedor e mês: repetir o POST no mesmo mês atualiza o registro
 * existente em vez de duplicar.</p>
 */
@RestController
@RequestMapping("/api/recebimento-cesta")
@Tag(name = "Recebimento de cesta", description = "Histórico e limite de cestas por recebedor")
public class RecebimentoCestaAPIController {

    private final RecebimentoCestaService recebimentoCestaService;
    private final RecebedorService recebedorService;
    private final Clock clock;

    /**
     * Construtor com injeção de dependências.
     *
     * @param recebimentoCestaService serviço de cestas
     * @param recebedorService        serviço de recebedores, para resolver documento
     * @param clock                   relógio da aplicação
     */
    public RecebimentoCestaAPIController(
            RecebimentoCestaService recebimentoCestaService,
            RecebedorService recebedorService,
            Clock clock
    ) {
        this.recebimentoCestaService = recebimentoCestaService;
        this.recebedorService = recebedorService;
        this.clock = clock;
    }

    /**
     * Verifica a elegibilidade pelo CPF ou RG.
     *
     * @param documento CPF ou RG
     * @param ano       ano de referência, opcional
     * @param mes       mês de referência de 1 a 12, opcional
     * @return elegibilidade do recebedor
     */
    @GetMapping("/elegibilidade")
    @Operation(summary = "Check basket eligibility by document")
    public ElegibilidadeCestaDTO elegibilidadePorDocumento(
            @RequestParam String documento,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes
    ) {
        List<RecebedorResponseDTO> encontrados = recebedorService.findByDocumento(documento);
        if (encontrados.isEmpty()) {
            throw new IllegalArgumentException("Nenhum recebedor encontrado para o documento informado");
        }
        if (encontrados.size() > 1) {
            throw new IllegalArgumentException("Mais de um recebedor encontrado para o documento informado");
        }
        return recebimentoCestaService.verificarElegibilidade(encontrados.get(0).id(), referencia(ano, mes));
    }

    /**
     * Verifica a elegibilidade de um recebedor pelo id.
     *
     * @param recebedorId identificador do recebedor
     * @param ano         ano de referência, opcional
     * @param mes         mês de referência de 1 a 12, opcional
     * @return elegibilidade do recebedor
     */
    @GetMapping("/elegibilidade/{recebedorId}")
    @Operation(summary = "Check basket eligibility by recipient id")
    public ElegibilidadeCestaDTO elegibilidadePorId(
            @PathVariable UUID recebedorId,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes
    ) {
        return recebimentoCestaService.verificarElegibilidade(recebedorId, referencia(ano, mes));
    }

    /**
     * Lista o histórico de cestas de um recebedor.
     *
     * @param recebedorId identificador do recebedor
     * @return histórico ordenado do mês mais recente para o mais antigo
     */
    @GetMapping("/historico/{recebedorId}")
    @Operation(summary = "List basket history by recipient id")
    public List<RecebimentoCestaResponseDTO> historico(@PathVariable UUID recebedorId) {
        return recebimentoCestaService.historico(recebedorId);
    }

    /**
     * Registra ou atualiza a cesta de um mês.
     *
     * @param recebedorId identificador do recebedor
     * @param dto         dados do recebimento
     * @return o registro salvo
     */
    @PostMapping("/{recebedorId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register or update a monthly basket")
    public RecebimentoCestaResponseDTO registrar(
            @PathVariable UUID recebedorId,
            @Valid @RequestBody RecebimentoCestaCreateDTO dto
    ) {
        return recebimentoCestaService.registrarOuAtualizar(recebedorId, dto);
    }

    private YearMonth referencia(Integer ano, Integer mes) {
        if (ano == null || mes == null) {
            return YearMonth.now(clock);
        }
        return YearMonth.of(ano, mes);
    }
}
