package com.vonlanplace.doacao.produto.tipo.controller;

import com.vonlanplace.doacao.produto.tipo.TipoProdutoCreateDTO;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoResponseDTO;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoService;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoUpdateDTO;
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

/**
 * Controller Thymeleaf de tipos de produto, prefixo {@code /tipo}.
 *
 * <p><strong>Espelha o controller de marca exatamente.</strong> Marca
 * e tipo são dados de referência com a mesma forma: chave é o nome, não há id, o
 * DTO tem um único campo, e apagar um que ainda está em uso é 409. Se as duas
 * telas divergirem, o usuário aprende duas regras para a mesma coisa.</p>
 */
@Controller
@RequestMapping("/tipo")
public class TipoProdutoViewController {

    /**
     * Quantidade de tipos por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/tipo/listar";

    private final TipoProdutoService tipoService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param tipoService serviço de tipos de produto
     */
    public TipoProdutoViewController(TipoProdutoService tipoService) {
        this.tipoService = tipoService;
    }

    /**
     * Lista tipos.
     *
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code tipo/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "nome",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<TipoProdutoResponseDTO> pagina = tipoService.findAll(pageable);

        model.addAttribute("titulo", "Tipos de produto");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        return "tipo/listar";
    }

    /**
     * Abre o formulário de cadastro em branco.
     *
     * @param model modelo da view
     * @return nome da view {@code tipo/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Novo tipo");
        model.addAttribute("tipoDTO", new TipoProdutoCreateDTO(""));
        return "tipo/form";
    }

    /**
     * Cadastra um tipo.
     *
     * @param tipoDTO            dados do formulário
     * @param resultado          erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("tipoDTO") TipoProdutoCreateDTO tipoDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            return "tipo/form";
        }

        tipoService.create(tipoDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Tipo cadastrado com sucesso.");
    }

    /**
     * Abre o formulário de edição.
     *
     * @param nome  nome atual do tipo, que é sua chave
     * @param model modelo da view
     * @return nome da view {@code tipo/form}
     */
    @GetMapping("/editar/{nome}")
    public String editar(@PathVariable String nome, Model model) {
        TipoProdutoResponseDTO tipo = tipoService.findByNome(nome);

        model.addAttribute("titulo", "Editar tipo");
        model.addAttribute("tipoDTO", new TipoProdutoUpdateDTO(tipo.nome()));
        model.addAttribute("nomeAtual", tipo.nome());
        return "tipo/form";
    }

    /**
     * Renomeia um tipo.
     *
     * @param nomeAtual          nome do tipo antes da alteração, que é a chave
     * @param tipoDTO            dados do formulário
     * @param resultado          erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{nome}")
    public String atualizar(
            @PathVariable String nomeAtual,
            @Valid @ModelAttribute("tipoDTO") TipoProdutoUpdateDTO tipoDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            return "tipo/form";
        }

        tipoService.update(nomeAtual, tipoDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Tipo atualizado com sucesso.");
    }

    /**
     * Apaga um tipo.
     *
     * <p>Tipo ainda em uso por algum produto é recusado com 409 pelo service, e
     * não pelo banco.</p>
     *
     * @param nome              nome do tipo
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{nome}")
    public String apagar(
            @PathVariable String nome,
            RedirectAttributes redirectAttributes
    ) {
        tipoService.delete(nome);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Tipo apagado com sucesso.");
    }
}
