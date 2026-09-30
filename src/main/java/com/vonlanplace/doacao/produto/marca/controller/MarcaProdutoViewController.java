package com.vonlanplace.doacao.produto.marca.controller;

import com.vonlanplace.doacao.produto.marca.MarcaProdutoCreateDTO;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoResponseDTO;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoService;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoUpdateDTO;
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
 * Controller Thymeleaf de marcas de produto, prefixo {@code /marca}.
 *
 * <p><strong>Identificador de marca é o nome, não um id.</strong> Marca é dado
 * de referência usado para classificar produto, e todo produto carrega o nome da
 * marca e não o id dela. Um id aqui exigiria um segundo lookup em toda
 * resolução, sem ganhar nada. As rotas de escrita recebem, portanto, o nome da
 * marca a editar ou apagar, codificado no path: {@code /editar/{nome}} e
 * {@code /apagar/{nome}}.</p>
 *
 * <p><strong>Escrita sempre por POST + redirect.</strong> Ver {@link Flash}: sem
 * redirect, F5 duplica a marca; sem flash, o usuário não recebe confirmação.</p>
 */
@Controller
@RequestMapping("/marca")
public class MarcaProdutoViewController {

    /**
     * Quantidade de marcas por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e nos argumentos dos fragmentos.
     */
    private static final String LISTAGEM = "/marca/listar";

    private final MarcaProdutoService marcaService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param marcaService serviço de marcas
     */
    public MarcaProdutoViewController(MarcaProdutoService marcaService) {
        this.marcaService = marcaService;
    }

    /**
     * Lista marcas.
     *
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code marca/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "nome",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<MarcaProdutoResponseDTO> pagina = marcaService.findAll(pageable);

        model.addAttribute("titulo", "Marcas");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        return "marca/listar";
    }

    /**
     * Abre o formulário de cadastro em branco.
     *
     * @param model modelo da view, que já recebe o DTO vazio
     * @return nome da view {@code marca/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Nova marca");
        model.addAttribute("marcaDTO", new MarcaProdutoCreateDTO(""));
        return "marca/form";
    }

    /**
     * Cadastra uma marca.
     *
     * @param marcaDTO           dados do formulário
     * @param resultado          erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("marcaDTO") MarcaProdutoCreateDTO marcaDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            return "marca/form";
        }

        marcaService.create(marcaDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Marca cadastrada com sucesso.");
    }

    /**
     * Abre o formulário de edição.
     *
     * @param nome  nome atual da marca, que é sua chave
     * @param model modelo da view
     * @return nome da view {@code marca/form}
     */
    @GetMapping("/editar/{nome}")
    public String editar(@PathVariable String nome, Model model) {
        MarcaProdutoResponseDTO marca = marcaService.findByNome(nome);

        model.addAttribute("titulo", "Editar marca");
        model.addAttribute("marcaDTO", new MarcaProdutoUpdateDTO(marca.nome()));
        model.addAttribute("nomeAtual", marca.nome());
        return "marca/form";
    }

    /**
     * Renomeia uma marca.
     *
     * @param nomeAtual          nome da marca antes da alteração, que é a chave
     * @param marcaDTO           dados do formulário
     * @param resultado          erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{nome}")
    public String atualizar(
            @PathVariable String nomeAtual,
            @Valid @ModelAttribute("marcaDTO") MarcaProdutoUpdateDTO marcaDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes
    ) {
        if (resultado.hasErrors()) {
            return "marca/form";
        }

        marcaService.update(nomeAtual, marcaDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Marca atualizada com sucesso.");
    }

    /**
     * Apaga uma marca.
     *
     * <p>Marca com produto vinculado não é apagada: o
     * {@link MarcaProdutoService} lança {@code IllegalStateException}, que o
     * advice de view traduz em 409 com a página de erro. A constraint de
     * integridade do banco é a segunda rede, não a primeira.</p>
     *
     * @param nome              nome da marca, que é sua chave
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{nome}")
    public String apagar(
            @PathVariable String nome,
            RedirectAttributes redirectAttributes
    ) {
        marcaService.delete(nome);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Marca apagada com sucesso.");
    }
}
