package com.vonlanplace.doacao.entrevistador.controller;

import com.vonlanplace.doacao.entrevistador.EntrevistadorCreateDTO;
import com.vonlanplace.doacao.entrevistador.EntrevistadorResponseDTO;
import com.vonlanplace.doacao.entrevistador.EntrevistadorService;
import com.vonlanplace.doacao.entrevistador.EntrevistadorUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * API REST de entrevistadores, prefixo {@code /api/entrevistador}.
 *
 * <p><strong>A senha entra em claro e não volta em claro.</strong> O
 * {@code EntrevistadorCreateDTO} aceita a senha porque é a credencial que o
 * cliente quer registrar, e o service a codifica antes de gravar. A resposta é
 * sempre um {@link EntrevistadorResponseDTO}, que não tem campo de senha: não
 * existe rota deste controller capaz de devolver hash, e o 201 de create tampouco.</p>
 *
 * <p>Este era o único recurso com entidade e repositório e sem nenhuma
 * superfície de leitura ou escrita além do {@code DoacaoService}, que resolve o
 * entrevistador por id.</p>
 */
@RestController
@RequestMapping("/api/entrevistador")
public class EntrevistadorAPIController {

    private final EntrevistadorService entrevistadorService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param entrevistadorService serviço de entrevistadores
     */
    public EntrevistadorAPIController(EntrevistadorService entrevistadorService) {
        this.entrevistadorService = entrevistadorService;
    }

    /**
     * Lista os entrevistadores, paginados.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @GetMapping
    public Page<EntrevistadorResponseDTO> getAll(Pageable pageable) {
        return entrevistadorService.findAll(pageable);
    }

    /**
     * Cadastra um entrevistador.
     *
     * @param createDTO dados de entrada, com a senha em claro
     * @return o entrevistador criado, sem a senha
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntrevistadorResponseDTO create(@Valid @RequestBody EntrevistadorCreateDTO createDTO) {
        return entrevistadorService.create(createDTO);
    }

    /**
     * Detalha um entrevistador.
     *
     * @param id identificador do entrevistador
     * @return DTO de resposta
     */
    @GetMapping("/{id}")
    public EntrevistadorResponseDTO getById(@PathVariable UUID id) {
        return entrevistadorService.findById(id);
    }

    /**
     * Detalha um entrevistador pelo e-mail de acesso.
     *
     * @param email e-mail do entrevistador
     * @return DTO de resposta
     */
    @GetMapping("/email/{email}")
    public EntrevistadorResponseDTO getByEmail(@PathVariable String email) {
        return entrevistadorService.findByEmail(email);
    }

    /**
     * Atualiza um entrevistador.
     *
     * @param id        identificador do entrevistador
     * @param updateDTO dados de entrada
     * @return o entrevistador atualizado, sem a senha
     */
    @PutMapping("/{id}")
    public EntrevistadorResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody EntrevistadorUpdateDTO updateDTO
    ) {
        return entrevistadorService.update(id, updateDTO);
    }

    /**
     * Apaga um entrevistador.
     *
     * @param id identificador do entrevistador
     * @return resposta sem corpo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        entrevistadorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
