package com.vonlanplace.doacao.recebedor.auxilio.controller;

import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioCreateDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioResponseDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioService;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioUpdateDTO;
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
 * Controller Thymeleaf de auxílios, prefixo {@code /auxilio}.
 *
 * <p>Registro financeiro simples: o auxílio é renda assistencial do recebedor
 * (Bolsa Família, ONG, etc.), não ligado aos campos {@code necessita*}. O total
 * por recebedor é exibido no detalhe do recebedor, junto com as despesas da
 * residência, para o entrevistador validar a cesta.</p>
 */
@Controller
@RequestMapping("/auxilio")
public class AuxilioViewController {

    /**
     * Quantidade de auxílios por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/auxilio/listar";

    private final AuxilioService auxilioService;
    private final RecebedorService recebedorService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param auxilioService   serviço de auxílios
     * @param recebedorService serviço de recebedores, para o select do formulário
     */
    public AuxilioViewController(
            AuxilioService auxilioService,
            RecebedorService recebedorService
    ) {
        this.auxilioService = auxilioService;
        this.recebedorService = recebedorService;
    }

    /**
     * Lista auxílios.
     *
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code auxilio/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "fonte",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<AuxilioResponseDTO> pagina = auxilioService.findAll(pageable);

        model.addAttribute("titulo", "Auxílios");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        return "auxilio/listar";
    }

    /**
     * Abre o formulário de cadastro.
     *
     * @param model modelo da view
     * @return nome da view {@code auxilio/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Novo auxílio");
        model.addAttribute("auxilioDTO", new AuxilioCreateDTO(null, "", null, null));
        popularOpcoes(model);
        return "auxilio/form";
    }

    /**
     * Cadastra um auxílio.
     *
     * @param auxilioDTO         dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular o select em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("auxilioDTO") AuxilioCreateDTO auxilioDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "auxilio/form";
        }

        auxilioService.create(auxilioDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Auxílio cadastrado com sucesso.");
    }

    /**
     * Abre o formulário de edição.
     *
     * @param id    identificador do auxílio
     * @param model modelo da view
     * @return nome da view {@code auxilio/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, Model model) {
        AuxilioResponseDTO auxilio = auxilioService.findById(id);

        model.addAttribute("titulo", "Editar auxílio");
        model.addAttribute("auxilioDTO", new AuxilioUpdateDTO(
                auxilio.valor(),
                auxilio.fonte(),
                auxilio.descricao(),
                auxilio.recebedor().id()
        ));
        model.addAttribute("id", auxilio.id());
        popularOpcoes(model);
        return "auxilio/form";
    }

    /**
     * Atualiza um auxílio.
     *
     * @param id                identificador do auxílio
     * @param auxilioDTO         dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular o select em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("auxilioDTO") AuxilioUpdateDTO auxilioDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        model.addAttribute("id", id);
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "auxilio/form";
        }

        auxilioService.update(id, auxilioDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Auxílio atualizado com sucesso.");
    }

    /**
     * Apaga um auxílio.
     *
     * @param id                identificador do auxílio
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{id}")
    public String apagar(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        auxilioService.delete(id);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Auxílio apagado com sucesso.");
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