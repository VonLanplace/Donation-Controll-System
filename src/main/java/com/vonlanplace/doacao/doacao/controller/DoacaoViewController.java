package com.vonlanplace.doacao.doacao.controller;

import com.vonlanplace.doacao.doacao.DoacaoCreateDTO;
import com.vonlanplace.doacao.doacao.DoacaoProdutoCreateDTO;
import com.vonlanplace.doacao.doacao.DoacaoResponseDTO;
import com.vonlanplace.doacao.doacao.DoacaoService;
import com.vonlanplace.doacao.doacao.DoacaoUpdateDTO;
import com.vonlanplace.doacao.entrevistador.EntrevistadorService;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoResponseDTO;
import com.vonlanplace.doacao.produto.ProdutoService;
import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.web.Flash;
import com.vonlanplace.doacao.web.Opcoes;
import com.vonlanplace.doacao.web.PaginaView;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
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

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller Thymeleaf de doações, prefixo {@code /doacao}.
 *
 * <p><strong>A doação é o único formulário do sistema com lista de itens, e a
 * lista é montada sem JavaScript.</strong> O formulário tem dois botões de
 * envio no mesmo {@code POST /doacao/form}: "Adicionar produto" valida a linha
 * nova e re-renderiza a página com a linha embaixo das anteriores; "Salvar
 * doação" manda o rascunho inteiro ao serviço e redireciona para a listagem. O
 * estado acumulado vive no bean {@link RascunhoDoacao} do escopo de sessão, não
 * em campos escondidos — cada POST re-renderiza a partir do rascunho que o
 * {@code @ModelAttribute} expõe.</p>
 *
 * <p>O rascunho é zerado a cada {@code GET /doacao/create} e a cada
 * {@code GET /doacao/editar/{id}}, de modo que um usuário que entre no meio do
 * caminho não herda uma doação meio montada de outra sessão de edição.</p>
 */
@Controller
@RequestMapping("/doacao")
public class DoacaoViewController {

    /**
     * Quantidade de doações por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * Rótulo do botão "Adicionar produto": valor do campo {@code acao}.
     */
    private static final String ACAO_ADICIONAR = "adicionar";

    /**
     * Rótulo do botão "Salvar doação": valor do campo {@code acao}.
     */
    private static final String ACAO_SALVAR = "salvar";

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/doacao/listar";

    private final DoacaoService doacaoService;
    private final RecebedorService recebedorService;
    private final EntrevistadorService entrevistadorService;
    private final ProdutoService produtoService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param doacaoService            serviço de doações
     * @param recebedorService         serviço de recebedores, para o select
     * @param entrevistadorService     serviço de entrevistadores, para o select
     * @param produtoService           serviço de produtos, para o select de linha e a lista de rótulos
     */
    public DoacaoViewController(
            DoacaoService doacaoService,
            RecebedorService recebedorService,
            EntrevistadorService entrevistadorService,
            ProdutoService produtoService
    ) {
        this.doacaoService = doacaoService;
        this.recebedorService = recebedorService;
        this.entrevistadorService = entrevistadorService;
        this.produtoService = produtoService;
    }

    /**
     * Expõe o rascunho da sessão a todas as views do controller, via
     * {@code th:object="${rascunho}"}.
     *
     * @param session a sessão HTTP do usuário, de onde o rascunho vem
     * @return o rascunho do usuário
     */
    @ModelAttribute("rascunho")
    public RascunhoDoacao rascunho(HttpSession session) {
        return RascunhoDoacao.daSessao(session);
    }

    /**
     * Lista doações.
     *
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code doacao/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "data",
                    direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<DoacaoResponseDTO> pagina = doacaoService.findAll(pageable);

        model.addAttribute("titulo", "Doações");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        return "doacao/listar";
    }

    /**
     * Detalha uma doação.
     *
     * @param id    identificador da doação
     * @param model modelo da view
     * @return nome da view {@code doacao/detalhe}
     */
    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable UUID id, Model model) {
        model.addAttribute("titulo", "Doação");
        model.addAttribute("doacao", doacaoService.findById(id));
        return "doacao/detalhe";
    }

    /**
     * Abre o formulário de cadastro, com um rascunho zerado.
     *
     * @param session a sessão HTTP do usuário
     * @param model    modelo da view
     * @return nome da view {@code doacao/form}
     */
    @GetMapping("/create")
    public String create(HttpSession session, Model model) {
        RascunhoDoacao.daSessao(session).reiniciar();
        model.addAttribute("titulo", "Nova doação");
        popularOpcoes(model);
        return "doacao/form";
    }

    /**
     * Abre o formulário de edição, semeando o rascunho com a doação atual.
     *
     * @param id      identificador da doação
     * @param session a sessão HTTP do usuário, de onde o rascunho vem
     * @param model   modelo da view
     * @return nome da view {@code doacao/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, HttpSession session, Model model) {
        DoacaoResponseDTO doacao = doacaoService.findById(id);
        RascunhoDoacao rascunho = RascunhoDoacao.daSessao(session);

        rascunho.reiniciar();
        rascunho.setId(doacao.id());
        rascunho.setData(doacao.data());
        rascunho.setNomeDoador(doacao.nomeDoador());
        rascunho.setRecebedorId(doacao.recebedor() == null ? null : doacao.recebedor().id());
        rascunho.setEntrevistadorId(doacao.entrevistador() == null ? null : doacao.entrevistador().id());
        doacao.produtos().forEach(p -> rascunho.getLinhas().add(
                new RascunhoDoacao.RascunhoLinha(
                        p.produto().id(), p.quantidade(), p.dataValidade()
                )
        ));

        model.addAttribute("titulo", "Editar doação");
        popularOpcoes(model);
        return "doacao/form";
    }

    /**
     * Recebe o POST único do formulário de doação e decide pelo botão apertado.
     *
     * <p>Dois botões, um só formulário, duas estradas — e o campo {@code acao}
     * decide qual delas. "Adicionar produto" valida a linha nova e devolve a
     * view, com a linha já incorporada ao rascunho; "Salvar doação" chama o
     * serviço e redireciona para a listagem. No meio, os dados de cabeçalho
     * (data, doador, recebedor, entrevistador) vêm do binding do rascunho; os
     * três campos da linha nova e exclusivos do POST são lidos por
     * {@code @RequestParam} cru, porque não têm dono no rascunho.</p>
     *
     * @param rascunho           o rascunho de sessão, com os campos ligados
     * @param resultado          erros de binding e de validação manuais
     * @param acao               rótulo do botão apertado
     * @param linhaProdutoId     produto da linha nova
     * @param linhaQuantidade    quantidade da linha nova
     * @param linhaDataValidade  validade da linha nova
     * @param redirectAttributes atributos do redirect de sucesso
     * @param model              modelo da view, para repopular selects em caso de re-render
     * @return redirect para a listagem, ou a própria view
     */
    @PostMapping("/form")
    public String form(
            @ModelAttribute("rascunho") RascunhoDoacao rascunho,
            BindingResult resultado,
            @RequestParam("acao") String acao,
            @RequestParam(value = "linhaProdutoId", required = false) String linhaProdutoId,
            @RequestParam(value = "linhaQuantidade", required = false) String linhaQuantidade,
            @RequestParam(value = "linhaDataValidade", required = false) String linhaDataValidade,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (ACAO_ADICIONAR.equals(acao)) {
            adicionarLinha(rascunho, resultado, linhaProdutoId, linhaQuantidade, linhaDataValidade);
            model.addAttribute("titulo", rascunho.getId() == null
                    ? "Nova doação"
                    : "Editar doação");
            popularOpcoes(model);
            return "doacao/form";
        }

        String saida = salvar(rascunho, resultado, redirectAttributes);
        if (resultado.hasErrors()) {
            model.addAttribute("titulo", rascunho.getId() == null
                    ? "Nova doação"
                    : "Editar doação");
            popularOpcoes(model);
            return "doacao/form";
        }
        return saida;
    }

    /**
     * Remove uma linha do rascunho.
     *
     * <p>Formulário próprio, separado do grande: o botão "Remover" de cada
     * linha não deve carregar os dados mal digitados ao redor. O rascunho de
     * sessão já tem o estado do último POST; aqui só se remove pelo índice e
     * se re-renderiza.</p>
     *
     * @param indice  posição da linha no rascunho
     * @param session a sessão HTTP do usuário, de onde o rascunho vem
     * @param model   modelo da view
     * @return nome da view {@code doacao/form}
     */
    @PostMapping("/linha/{indice}/remover")
    public String removerLinha(@PathVariable int indice, HttpSession session, Model model) {
        RascunhoDoacao rascunho = RascunhoDoacao.daSessao(session);

        if (indice >= 0 && indice < rascunho.getLinhas().size()) {
            rascunho.getLinhas().remove(indice);
        }
        model.addAttribute("titulo", rascunho.getId() == null
                ? "Nova doação"
                : "Editar doação");
        popularOpcoes(model);
        return "doacao/form";
    }

    /**
     * Apaga uma doação. Os itens saem junto, pelo cascade do agregado.
     *
     * @param id                identificador da doação
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{id}")
    public String apagar(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        doacaoService.delete(id);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Doação apagada com sucesso.");
    }

    /**
     * Valida a linha nova e, se válida, adiciona-a ao rascunho; senão grava um
     * erro global para o fragmento de erros mostrar.
     *
     * @param rascunho          o rascunho de sessão
     * @param resultado         erros ligados ao rascunho
     * @param produtoId         id do produto, como veio do formulário
     * @param quantidade        quantidade, como veio do formulário
     * @param dataValidade      validade, como veio do formulário
     */
    private void adicionarLinha(
            RascunhoDoacao rascunho,
            BindingResult resultado,
            String produtoId,
            String quantidade,
            String dataValidade
    ) {
        UUID idProduto = tentarUuid(produtoId);
        int qtd = tentarInt(quantidade);
        LocalDate validade = tentarData(dataValidade);

        boolean existe = rascunho.getLinhas().stream().anyMatch(l -> l.produtoId().equals(idProduto));
        if (existe) {
            resultado.reject("linhaDuplicada", "Este produto já está na doação.");
        } else if (idProduto == null || qtd <= 0 || validade == null) {
            resultado.reject(
                    "linhaInvalida",
                    "Informe o produto, uma quantidade acima de zero e a data de validade."
            );
        } else {
            rascunho.getLinhas().add(new RascunhoDoacao.RascunhoLinha(idProduto, qtd, validade));
        }
    }

    /**
     * Valida o cabeçalho, monta o DTO e chama o serviço. Em caso de sucesso, o
     * rascunho é zerado e o redirect com a mensagem de flash é devolvido.
     *
     * @param rascunho          o rascunho de sessão
     * @param resultado         erros ligados ao rascunho
     * @param redirectAttributes atributos do redirect de sucesso
     * @return redirect para a listagem, ou {@code null} se houver erro
     */
    private String salvar(
            RascunhoDoacao rascunho,
            BindingResult resultado,
            RedirectAttributes redirectAttributes
    ) {
        if (rascunho.getData() == null) {
            resultado.rejectValue("data", "obrigatoria", "A data da doação é obrigatória.");
            return null;
        }

        List<DoacaoProdutoCreateDTO> itens = rascunho.getLinhas().stream()
                .map(l -> new DoacaoProdutoCreateDTO(l.produtoId(), l.quantidade(), l.dataValidade()))
                .toList();

        try {
            if (rascunho.getId() == null) {
                doacaoService.create(new DoacaoCreateDTO(
                        rascunho.getData(),
                        rascunho.getNomeDoador(),
                        rascunho.getRecebedorId(),
                        rascunho.getEntrevistadorId(),
                        itens
                ));
            } else {
                doacaoService.update(rascunho.getId(), new DoacaoUpdateDTO(
                        rascunho.getData(),
                        rascunho.getNomeDoador(),
                        rascunho.getRecebedorId(),
                        rascunho.getEntrevistadorId(),
                        itens
                ));
            }
        } catch (EntityNotFoundException e) {
            resultado.reject("vinculoInexistente", e.getMessage());
            return null;
        }

        rascunho.reiniciar();
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Doação salva com sucesso.");
    }

    /**
     * Converte a string vinda do formulário em {@link UUID}, ou devolve
     * {@code null}.
     *
     * @param texto texto do formulário
     * @return UUID, ou {@code null} se vazio ou malformado
     */
    private UUID tentarUuid(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(texto);
        } catch (IllegalArgumentException ignorado) {
            return null;
        }
    }

    /**
     * Converte a string vinda do formulário em inteiro, ou devolve zero.
     *
     * @param texto texto do formulário
     * @return inteiro, ou zero se vazio ou malformado
     */
    private int tentarInt(String texto) {
        if (texto == null || texto.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ignorado) {
            return 0;
        }
    }

    /**
     * Converte a string vinda do formulário em data, ou devolve {@code null}.
     *
     * @param texto texto do formulário
     * @return data, ou {@code null} se vazia ou malformada
     */
    private LocalDate tentarData(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException ignorado) {
            return null;
        }
    }

    /**
     * Coloca no modelo tudo o que o formulário precisa para renderizar: os três
     * selects (recebedor, entrevistador, produto da linha nova) e os rótulos de
     * exibição da tabela de linhas já adicionadas.
     *
     * <p>Chamado só nos caminhos que renderizam o formulário. Nas páginas de
     * listagem e detalhe essas três listas inteiras seriam dinheiro jogado
     * fora — o catálogo de produtos é grande de propósito.</p>
     *
     * @param model modelo da view
     */
    private void popularOpcoes(Model model) {
        model.addAttribute("recebedores", Opcoes.deItens(
                recebedorService.findAll(),
                r -> r.id().toString(),
                r -> r.nomeCompleto()
        ));
        model.addAttribute("entrevistadores", Opcoes.deItens(
                entrevistadorService.findAll(),
                e -> e.id().toString(),
                e -> e.nome()
        ));
        model.addAttribute("produtos", Opcoes.deItens(
                produtoService.findAll(),
                p -> p.id().toString(),
                p -> rotuloProduto(p.codigoBarras(), p.marcaProduto())
        ));

        Map<UUID, String> rotulos = new LinkedHashMap<>();
        for (var p : produtoService.findAll()) {
            rotulos.put(p.id(), rotuloProduto(p.codigoBarras(), p.marcaProduto()));
        }
        model.addAttribute("rotulosProduto", rotulos);
    }

    /**
     * Monta o rótulo de exibição de um produto: código de barras e, quando há
     * marca, a marca logo atrás.
     *
     * @param codigoBarras  código de barras do produto
     * @param marcaProduto  marca aninhada, possivelmente nula
     * @return rótulo pronto para select ou tabela
     */
    private String rotuloProduto(String codigoBarras, MarcaProdutoResponseDTO marcaProduto) {
        return codigoBarras + (marcaProduto != null ? " · " + marcaProduto.nome() : "");
    }
}