package com.vonlanplace.doacao.produto.controller;

import com.vonlanplace.doacao.produto.ProdutoResponseDTO;
import com.vonlanplace.doacao.produto.ProdutoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller Thymeleaf de produtos, prefixo {@code /produto}.
 *
 * <p><strong>Responsável só por leitura.</strong> As rotas de escrita
 * ({@code /create} e {@code /update}) foram removidas em vez de consertadas: ambas
 * devolviam a view {@code produto/form}, que não existe em
 * {@code src/main/resources/templates/}, e portanto respondiam 500 desde sempre.
 * Manter rotas que só produzem erro enquanto se diz que está consertando a base é
 * pior do que não tê-las. Elas voltam no incremento em que o formulário existir,
 * junto com o tratamento de validação e dos 409 que a API já produz.</p>
 *
 * <p><strong>A API equivalente está em {@link ProdutoAPIController}</strong> e
 * funciona. Este controller chama o mesmo {@link ProdutoService} direto, sem passar
 * por HTTP — não há CORS para configurar e nenhum dado pessoal trafega em JSON.</p>
 */
@Controller
@RequestMapping("/produto")
public class ProdutoViewController {

    /**
     * Quantidade de produtos por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    private final ProdutoService produtoService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param produtoService serviço de produtos
     */
    public ProdutoViewController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Lista produtos.
     *
     * <p>A ordenação tem padrão vindo do próprio {@code @PageableDefault}, e não é
     * fixada no código, para que os links de paginação montados pelo fragmento
     * realmente troquem a página. A versão anterior desta classe, e o
     * {@code ProdutoAPIController}, deixam a ordenação a cargo do cliente.</p>
     *
     * @param pageable paginação montada pelo Spring a partir de {@code ?page=},
     *                 {@code ?size=} e {@code ?sort=}
     * @param model    modelo da view
     * @return nome da view {@code produto/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "codigoBarras",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<ProdutoResponseDTO> pagina = produtoService.findAll(pageable);

        model.addAttribute("titulo", "Produtos");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", sortComoQuery(pageable));
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
     * Converte a ordenação da requisição no formato de query param.
     *
     * <p>Necessário porque {@code page.sort} é um objeto {@code Sort}. Se fosse
     * interpolado direto no link de paginação, chamaria {@code toString()} e
     * produziria {@code "codigoBarras: ASC"} — que não é um valor válido de
     * {@code ?sort=}, e a ordenação seria perdida ao trocar de página.</p>
     *
     * @param pageable paginação recebida
     * @return ordenação no formato {@code propriedade,direcao[,propriedade,direcao]}
     */
    private String sortComoQuery(Pageable pageable) {
        return pageable.getSort().stream()
                .map(ordem -> ordem.getProperty() + "," + ordem.getDirection().name().toLowerCase())
                .collect(Collectors.joining(","));
    }
}
