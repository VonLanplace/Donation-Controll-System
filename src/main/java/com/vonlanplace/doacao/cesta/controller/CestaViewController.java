package com.vonlanplace.doacao.cesta.controller;

import com.vonlanplace.doacao.cesta.CestaCreateDTO;
import com.vonlanplace.doacao.cesta.CestaResponseDTO;
import com.vonlanplace.doacao.cesta.CestaService;
import com.vonlanplace.doacao.cesta.CestaUpdateDTO;
import com.vonlanplace.doacao.recebedor.RecebedorService;
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
 * Controller Thymeleaf de cestas, prefixo {@code /cesta}.
 *
 * <p><strong>A cesta é a entrega de uma cesta básica a um recebedor, e o
 * recebedor é opcional.</strong> Uma cesta pode ser registrada antes de existir
 * o nome de quem vai recebê-la, e o update pode desligar o vínculo. É por isso
 * que o select de recebedor tem o item "Selecione…" com valor vazio, e por isso
 * o {@code th:field} sobre o {@code UUID} aceita o vazio e envia
 * {@code null}.</p>
 *
 * <p><strong>A data de entrega é opcional e "não tocada" quando vazia.</strong>
 * Disparidade deliberada com o recebedor, que o service trata: campo vazio no
 * update mantém a data anterior, enquanto {@code recebedorId} nulo desliga o
 * vínculo. As duas regras já estavam na API, e a tela não muda nenhuma.</p>
 */
@Controller
@RequestMapping("/cesta")
public class CestaViewController {

    /**
     * Quantidade de cestas por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/cesta/listar";

    private final CestaService cestaService;
    private final RecebedorService recebedorService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param cestaService      serviço de cestas
     * @param recebedorService  serviço de recebedores, para o select do formulário
     */
    public CestaViewController(
            CestaService cestaService,
            RecebedorService recebedorService
    ) {
        this.cestaService = cestaService;
        this.recebedorService = recebedorService;
    }

    /**
     * Lista cestas.
     *
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code cesta/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "dataEntrega",
                    direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        Page<CestaResponseDTO> pagina = cestaService.findAll(pageable);

        model.addAttribute("titulo", "Cestas");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        return "cesta/listar";
    }

    /**
     * Detalha uma cesta.
     *
     * @param id    identificador da cesta
     * @param model modelo da view
     * @return nome da view {@code cesta/detalhe}
     */
    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable UUID id, Model model) {
        model.addAttribute("titulo", "Cesta");
        model.addAttribute("cesta", cestaService.findById(id));
        return "cesta/detalhe";
    }

    /**
     * Abre o formulário de cadastro.
     *
     * @param model modelo da view
     * @return nome da view {@code cesta/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Nova cesta");
        model.addAttribute("cestaDTO", new CestaCreateDTO(null, null));
        popularOpcoes(model);
        return "cesta/form";
    }

    /**
     * Cadastra uma cesta.
     *
     * @param cestaDTO           dados do formulário
     * @param resultado          erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model              modelo da view, para repopular o select em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("cestaDTO") CestaCreateDTO cestaDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "cesta/form";
        }

        cestaService.create(cestaDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Cesta cadastrada com sucesso.");
    }

    /**
     * Abre o formulário de edição.
     *
     * @param id    identificador da cesta
     * @param model modelo da view
     * @return nome da view {@code cesta/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, Model model) {
        CestaResponseDTO cesta = cestaService.findById(id);

        model.addAttribute("titulo", "Editar cesta");
        model.addAttribute("cestaDTO", new CestaUpdateDTO(
                cesta.dataEntrega(),
                cesta.recebedor() == null ? null : cesta.recebedor().id()
        ));
        model.addAttribute("id", cesta.id());
        popularOpcoes(model);
        return "cesta/form";
    }

    /**
     * Atualiza uma cesta.
     *
     * @param id                identificador da cesta
     * @param cestaDTO           dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular o select em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("cestaDTO") CestaUpdateDTO cestaDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        model.addAttribute("id", id);
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "cesta/form";
        }

        cestaService.update(id, cestaDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Cesta atualizada com sucesso.");
    }

    /**
     * Apaga uma cesta.
     *
     * <p>Nada referencia a cesta, então a remoção é sempre livre.</p>
     *
     * @param id                identificador da cesta
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{id}")
    public String apagar(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        cestaService.delete(id);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Cesta apagada com sucesso.");
    }

    /**
     * Coloca no modelo as opções do select de recebedores.
     *
     * <p>O select é opcional, então o "Selecione…" com valor vazio que o
     * fragmento emite é parte do contrato: é assim que um UUID nulo chega ao
     * service, e é assim que o vínculo se desliga no update.</p>
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