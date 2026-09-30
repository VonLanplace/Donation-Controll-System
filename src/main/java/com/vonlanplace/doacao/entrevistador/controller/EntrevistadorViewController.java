package com.vonlanplace.doacao.entrevistador.controller;

import com.vonlanplace.doacao.entrevistador.EntrevistadorCreateDTO;
import com.vonlanplace.doacao.entrevistador.EntrevistadorResponseDTO;
import com.vonlanplace.doacao.entrevistador.EntrevistadorService;
import com.vonlanplace.doacao.entrevistador.EntrevistadorUpdateDTO;
import com.vonlanplace.doacao.web.Flash;
import com.vonlanplace.doacao.web.PaginaView;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controller Thymeleaf de entrevistadores, prefixo {@code /entrevistador}.
 *
 * <p><strong>O formulário de edição deixa a senha em branco de propósito.</strong>
 * Campo vazio é "não mexer na senha", e o service trata assim. Se o campo
 * viesse preenchido, cada edição de nome apagaria a senha da pessoa, e o
 * efeito só apareceria no login seguinte.</p>
 *
 * <p>Nenhuma tela deste controller mostra hash. O que sai daqui é sempre
 * {@link EntrevistadorResponseDTO}, que tem três campos.</p>
 */
@Controller
@RequestMapping("/entrevistador")
public class EntrevistadorViewController {

    /**
     * Quantidade de entrevistadores por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/entrevistador/listar";

    private final EntrevistadorService entrevistadorService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param entrevistadorService serviço de entrevistadores
     */
    public EntrevistadorViewController(EntrevistadorService entrevistadorService) {
        this.entrevistadorService = entrevistadorService;
    }

    /**
     * Lista entrevistadores.
     *
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code entrevistador/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "nome",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<EntrevistadorResponseDTO> pagina = entrevistadorService.findAll(pageable);

        model.addAttribute("titulo", "Entrevistadores");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        return "entrevistador/listar";
    }

    /**
     * Detalha um entrevistador.
     *
     * @param id    identificador do entrevistador
     * @param model modelo da view
     * @return nome da view {@code entrevistador/detalhe}
     */
    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable UUID id, Model model) {
        model.addAttribute("titulo", "Entrevistador");
        model.addAttribute("entrevistador", entrevistadorService.findById(id));
        return "entrevistador/detalhe";
    }

    /**
     * Abre o formulário de cadastro.
     *
     * @param model modelo da view
     * @return nome da view {@code entrevistador/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Novo entrevistador");
        model.addAttribute("entrevistadorDTO", new EntrevistadorCreateDTO("", "", ""));
        return "entrevistador/form";
    }

    /**
     * Cadastra um entrevistador.
     *
     * @param entrevistadorDTO   dados do formulário, com a senha em claro
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("entrevistadorDTO") EntrevistadorCreateDTO entrevistadorDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            return "entrevistador/form";
        }

        entrevistadorService.create(entrevistadorDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Entrevistador cadastrado com sucesso.");
    }

    /**
     * Abre o formulário de edição, com a senha em branco.
     *
     * @param id    identificador do entrevistador
     * @param model modelo da view
     * @return nome da view {@code entrevistador/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, Model model) {
        EntrevistadorResponseDTO entrevistador = entrevistadorService.findById(id);

        model.addAttribute("titulo", "Editar entrevistador");
        model.addAttribute("entrevistadorDTO", new EntrevistadorUpdateDTO(
                entrevistador.nome(),
                entrevistador.email(),
                ""
        ));
        model.addAttribute("id", entrevistador.id());
        return "entrevistador/form";
    }

    /**
     * Atualiza um entrevistador.
     *
     * @param id                identificador do entrevistador
     * @param entrevistadorDTO   dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("entrevistadorDTO") EntrevistadorUpdateDTO entrevistadorDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            return "entrevistador/form";
        }

        entrevistadorService.update(id, entrevistadorDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Entrevistador atualizado com sucesso.");
    }

    /**
     * Apaga um entrevistador.
     *
     * @param id                identificador do entrevistador
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{id}")
    public String apagar(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        entrevistadorService.delete(id);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Entrevistador apagado com sucesso.");
    }
}
