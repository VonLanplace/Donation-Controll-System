package com.vonlanplace.doacao.recebedor.residencia.despesa.controller;

import com.vonlanplace.doacao.recebedor.residencia.ResidenciaService;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaCreateDTO;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaService;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaUpdateDTO;
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
 * Controller Thymeleaf de despesas, prefixo {@code /despesa}.
 *
 * <p>Registro financeiro simples: a despesa pertence a uma residência, e o
 * formulário carrega o select de residências. O total por residência é exibido
 * no detalhe do recebedor, junto com o total de auxílios, para o entrevistador
 * fechar a conta antes de decidir a cesta.</p>
 */
@Controller
@RequestMapping("/despesa")
public class DespesaViewController {

    /**
     * Quantidade de despesas por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/despesa/listar";

    private final DespesaService despesaService;
    private final ResidenciaService residenciaService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param despesaService     serviço de despesas
     * @param residenciaService  serviço de residências, para o select do formulário
     */
    public DespesaViewController(
            DespesaService despesaService,
            ResidenciaService residenciaService
    ) {
        this.despesaService = despesaService;
        this.residenciaService = residenciaService;
    }

    /**
     * Lista despesas.
     *
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code despesa/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "fonte",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<DespesaResponseDTO> pagina = despesaService.findAll(pageable);

        model.addAttribute("titulo", "Despesas");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        return "despesa/listar";
    }

    /**
     * Abre o formulário de cadastro.
     *
     * @param model modelo da view
     * @return nome da view {@code despesa/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Nova despesa");
        model.addAttribute("despesaDTO", new DespesaCreateDTO(null, "", null, null));
        popularOpcoes(model);
        return "despesa/form";
    }

    /**
     * Cadastra uma despesa.
     *
     * @param despesaDTO         dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular o select em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("despesaDTO") DespesaCreateDTO despesaDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "despesa/form";
        }

        despesaService.create(despesaDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Despesa cadastrada com sucesso.");
    }

    /**
     * Abre o formulário de edição.
     *
     * @param id    identificador da despesa
     * @param model modelo da view
     * @return nome da view {@code despesa/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, Model model) {
        DespesaResponseDTO despesa = despesaService.findById(id);

        model.addAttribute("titulo", "Editar despesa");
        model.addAttribute("despesaDTO", new DespesaUpdateDTO(
                despesa.valor(),
                despesa.fonte(),
                despesa.descricao(),
                despesa.residencia().id()
        ));
        model.addAttribute("id", despesa.id());
        popularOpcoes(model);
        return "despesa/form";
    }

    /**
     * Atualiza uma despesa.
     *
     * @param id                identificador da despesa
     * @param despesaDTO         dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular o select em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("despesaDTO") DespesaUpdateDTO despesaDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        model.addAttribute("id", id);
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "despesa/form";
        }

        despesaService.update(id, despesaDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Despesa atualizada com sucesso.");
    }

    /**
     * Apaga uma despesa.
     *
     * @param id                identificador da despesa
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{id}")
    public String apagar(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        despesaService.delete(id);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Despesa apagada com sucesso.");
    }

    /**
     * Coloca no modelo as opções do select de residências.
     *
     * @param model modelo da view
     */
    private void popularOpcoes(Model model) {
        model.addAttribute("residencias", Opcoes.deItens(
                residenciaService.findAll(),
                r -> r.id().toString(),
                r -> r.logradouro() + ", " + r.numero()
        ));
    }
}