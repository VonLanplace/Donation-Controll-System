package com.vonlanplace.doacao.recebedor.dependente.controller;

import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.recebedor.dependente.DependenteCreateDTO;
import com.vonlanplace.doacao.recebedor.dependente.DependenteResponseDTO;
import com.vonlanplace.doacao.recebedor.dependente.DependenteService;
import com.vonlanplace.doacao.recebedor.dependente.DependenteUpdateDTO;
import com.vonlanplace.doacao.web.Flash;
import com.vonlanplace.doacao.web.Opcoes;
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
 * Controller Thymeleaf de dependentes, prefixo {@code /dependente}.
 *
 * <p>Registro simples de entrevista: cada recebedor tem N dependentes, e a
 * tela é o cadastro avulso com select de recebedor. O fluxo natural começa no
 * perfil do recebedor, que já lista quantos dependentes a família tem.</p>
 */
@Controller
@RequestMapping("/dependente")
public class DependenteViewController {

    /**
     * Quantidade de dependentes por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/dependente/listar";

    private final DependenteService dependenteService;
    private final RecebedorService recebedorService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param dependenteService serviço de dependentes
     * @param recebedorService  serviço de recebedores, para o select do formulário
     */
    public DependenteViewController(
            DependenteService dependenteService,
            RecebedorService recebedorService
    ) {
        this.dependenteService = dependenteService;
        this.recebedorService = recebedorService;
    }

    /**
     * Lista dependentes.
     *
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code dependente/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "nome",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<DependenteResponseDTO> pagina = dependenteService.findAll(pageable);

        model.addAttribute("titulo", "Dependentes");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        return "dependente/listar";
    }

    /**
     * Abre o formulário de cadastro.
     *
     * @param model modelo da view
     * @return nome da view {@code dependente/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Novo dependente");
        model.addAttribute("dependenteDTO", new DependenteCreateDTO("", 0, null, null));
        popularOpcoes(model);
        return "dependente/form";
    }

    /**
     * Cadastra um dependente.
     *
     * @param dependenteDTO      dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular o select em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("dependenteDTO") DependenteCreateDTO dependenteDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "dependente/form";
        }

        dependenteService.create(dependenteDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Dependente cadastrado com sucesso.");
    }

    /**
     * Abre o formulário de edição.
     *
     * @param id    identificador do dependente
     * @param model modelo da view
     * @return nome da view {@code dependente/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, Model model) {
        DependenteResponseDTO dependente = dependenteService.findById(id);

        model.addAttribute("titulo", "Editar dependente");
        model.addAttribute("dependenteDTO", new DependenteUpdateDTO(
                dependente.nome(),
                dependente.idade(),
                dependente.dataNascimento(),
                dependente.recebedor().id()
        ));
        model.addAttribute("id", dependente.id());
        popularOpcoes(model);
        return "dependente/form";
    }

    /**
     * Atualiza um dependente.
     *
     * @param id                identificador do dependente
     * @param dependenteDTO      dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular o select em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("dependenteDTO") DependenteUpdateDTO dependenteDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        model.addAttribute("id", id);
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "dependente/form";
        }

        dependenteService.update(id, dependenteDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Dependente atualizado com sucesso.");
    }

    /**
     * Apaga um dependente.
     *
     * @param id                identificador do dependente
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{id}")
    public String apagar(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        dependenteService.delete(id);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Dependente apagado com sucesso.");
    }

    /**
     * Coloca no modelo as opções do select de recebedores.
     *
     * @param model modelo da view
     */
    private void popularOpcoes(Model model) {
        model.addAttribute("recebedores", Opcoes.deItens(
                recebedorService.findAll(),
                r -> r.id().toString(),
                r -> r.nomeCompleto()
        ));
    }
}