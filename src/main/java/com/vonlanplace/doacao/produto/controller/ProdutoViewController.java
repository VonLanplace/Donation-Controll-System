package com.vonlanplace.doacao.produto.controller;

import com.vonlanplace.doacao.produto.ProdutoCreateDTO;
import com.vonlanplace.doacao.produto.ProdutoResponseDTO;
import com.vonlanplace.doacao.produto.ProdutoService;
import com.vonlanplace.doacao.produto.ProdutoUpdateDTO;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoService;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controller Thymeleaf de produtos, prefixo {@code /produto}.
 *
 * <p><strong>Marca e tipo são escolhidos pelo nome, não pelo id.</strong> É por
 * isso que o DTO do produto tem {@code String marcaProduto} e {@code String
 * tipoProduto}, enquanto a resposta aninha só o nome de cada um, e é por isso
 * que os dois {@code <select>} do formulário são montados com
 * {@link MarcaProdutoService#listaNomes()} e
 * {@link TipoProdutoService#listaNomes()}: o service resolve a entidade pelo
 * nome antes de gravar.</p>
 *
 * <p><strong>Escrita sempre por POST + redirect.</strong> Ver {@link Flash}.</p>
 *
 * <p>A API equivalente está em {@link ProdutoAPIController} e funciona. Este
 * controller chama o mesmo {@link ProdutoService} direto, sem passar por HTTP —
 * não há CORS para configurar e nenhum dado pessoal trafega em JSON.</p>
 */
@Controller
@RequestMapping("/produto")
public class ProdutoViewController {

    /**
     * Quantidade de produtos por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/produto/listar";

    private final ProdutoService produtoService;
    private final MarcaProdutoService marcaService;
    private final TipoProdutoService tipoService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param produtoService serviço de produtos
     * @param marcaService   serviço de marcas, para o select do formulário
     * @param tipoService    serviço de tipos, para o select do formulário
     */
    public ProdutoViewController(
            ProdutoService produtoService,
            MarcaProdutoService marcaService,
            TipoProdutoService tipoService
    ) {
        this.produtoService = produtoService;
        this.marcaService = marcaService;
        this.tipoService = tipoService;
    }

    /**
     * Lista produtos, opcionalmente filtrando por código de barras.
     *
     * <p>A ordenação tem padrão vindo do próprio {@code @PageableDefault}, e não é
     * fixada no código, para que os links de paginação montados pelo fragmento
     * realmente troquem a página. A versão anterior desta classe, e o
     * {@code ProdutoAPIController}, deixam a ordenação a cargo do cliente.</p>
     *
     * @param busca         prefixo do código de barras, opcional
     * @param pageable      paginação montada pelo Spring a partir de {@code ?page=},
     *                      {@code ?size=} e {@code ?sort=}
     * @param model         modelo da view
     * @return nome da view {@code produto/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @RequestParam(name = "busca", required = false) String busca,
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "codigoBarras",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<ProdutoResponseDTO> pagina = busca == null || busca.isBlank()
                ? produtoService.findAll(pageable)
                : produtoService.findByCodigoBarras(busca, pageable);

        model.addAttribute("titulo", "Produtos");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        model.addAttribute("busca", busca == null ? "" : busca);
        return "produto/listar";
    }

    /**
     * Detalha um produto.
     *
     * @param id    identificador do produto
     * @param model modelo da view
     * @return nome da view {@code produto/detalhe}
     * @throws jakarta.persistence.EntityNotFoundException se o produto não existir,
     *                                                    traduzida em 404 pelo advice
     */
    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable UUID id, Model model) {
        model.addAttribute("titulo", "Produto");
        model.addAttribute("produto", produtoService.findById(id));
        return "produto/detalhe";
    }

    /**
     * Abre o formulário de cadastro em branco.
     *
     * @param model modelo da view
     * @return nome da view {@code produto/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Novo produto");
        model.addAttribute("produtoDTO", new ProdutoCreateDTO("", "", ""));
        popularOpcoes(model);
        return "produto/form";
    }

    /**
     * Cadastra um produto.
     *
     * @param produtoDTO        dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular os selects em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("produtoDTO") ProdutoCreateDTO produtoDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "produto/form";
        }

        produtoService.create(produtoDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Produto cadastrado com sucesso.");
    }

    /**
     * Abre o formulário de edição.
     *
     * @param id    identificador do produto
     * @param model modelo da view
     * @return nome da view {@code produto/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, Model model) {
        ProdutoResponseDTO produto = produtoService.findById(id);

        model.addAttribute("titulo", "Editar produto");
        model.addAttribute("produtoDTO", new ProdutoUpdateDTO(
                produto.codigoBarras(),
                produto.marcaProduto() == null ? "" : produto.marcaProduto().nome(),
                produto.tipoProduto() == null ? "" : produto.tipoProduto().nome()
        ));
        model.addAttribute("id", produto.id());
        popularOpcoes(model);
        return "produto/form";
    }

    /**
     * Atualiza um produto.
     *
     * @param id                identificador do produto
     * @param produtoDTO        dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular os selects em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("produtoDTO") ProdutoUpdateDTO produtoDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        model.addAttribute("id", id);
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "produto/form";
        }

        produtoService.update(id, produtoDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Produto atualizado com sucesso.");
    }

    /**
     * Apaga um produto.
     *
     * <p>Produto não tem dependência que impeça a remoção: ele é folha no modelo,
     * referenciado por DoacaoProduto, que se desliga por cascade a partir de
     * Doacao. Se a constraint de integridade for violada, o advice traduz em
     * 409.</p>
     *
     * @param id                identificador do produto
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{id}")
    public String apagar(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        produtoService.delete(id);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Produto apagado com sucesso.");
    }

    /**
     * Coloca no modelo as opções dos dois selects de relação.
     *
     * <p>Chamado nas seis rotas que renderizam o formulário, inclusive as que
     * devolvem a view por causa de erro de validação. Sem isso, o formulário que
     * volta com erro teria os selects vazios e o usuário perderia a seleção que
     * acabou de fazer.</p>
     *
     * @param model modelo da view
     */
    private void popularOpcoes(Model model) {
        model.addAttribute("marcas", Opcoes.deValores(marcaService.listaNomes()));
        model.addAttribute("tipos", Opcoes.deValores(tipoService.listaNomes()));
    }
}
