package com.vonlanplace.doacao.recebedor.residencia.controller;

import com.vonlanplace.doacao.recebedor.residencia.ResidenciaCreateDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaService;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaUpdateDTO;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
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
 * Controller Thymeleaf de residências, prefixo {@code /residencia}.
 *
 * <p><strong>Aqui a chave é o id, e não o nome.</strong> Residência tem
 * endereço completo com número, complemento e bairro, e unlike marca e tipo
 * ninguém identifica uma residência pelo logradouro: duas casas na mesma rua
 * têm o mesmo logradouro. O id é o que o recebedor referencia.</p>
 *
 * <p><strong>Update é o formulário inteiro, não um patch.</strong>
 * {@code ResidenciaUpdateDTO} repete os dez campos do create e não tem id: quem
 * chama decide o alvo pela URL. É por isso que o template reaproveita o mesmo
 * form para os dois casos e o controller só troca a action.</p>
 *
 * <p>Os dois enums viram select por {@link Opcoes#deEnum(Class)}, que grava
 * {@code name()} e exibe a descrição em português.</p>
 */
@Controller
@RequestMapping("/residencia")
public class ResidenciaViewController {

    /**
     * Quantidade de residências por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/residencia/listar";

    private final ResidenciaService residenciaService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param residenciaService serviço de residências
     */
    public ResidenciaViewController(ResidenciaService residenciaService) {
        this.residenciaService = residenciaService;
    }

    /**
     * Lista residências, opcionalmente filtrando por cidade.
     *
     * @param busca    nome da cidade, opcional
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code residencia/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @RequestParam(name = "busca", required = false) String busca,
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "cidade",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<ResidenciaResponseDTO> pagina = busca == null || busca.isBlank()
                ? residenciaService.findAll(pageable)
                : residenciaService.findByCidade(busca, pageable);

        model.addAttribute("titulo", "Residências");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        model.addAttribute("busca", busca == null ? "" : busca);
        return "residencia/listar";
    }

    /**
     * Detalha uma residência.
     *
     * @param id    identificador da residência
     * @param model modelo da view
     * @return nome da view {@code residencia/detalhe}
     */
    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable UUID id, Model model) {
        model.addAttribute("titulo", "Residência");
        model.addAttribute("residencia", residenciaService.findById(id));
        return "residencia/detalhe";
    }

    /**
     * Abre o formulário de cadastro em branco.
     *
     * @param model modelo da view
     * @return nome da view {@code residencia/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Nova residência");
        model.addAttribute("residenciaDTO", new ResidenciaCreateDTO(
                "", "", "", "", "", "", 0, 0, null, null
        ));
        popularOpcoes(model);
        return "residencia/form";
    }

    /**
     * Cadastra uma residência.
     *
     * @param residenciaDTO      dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular os selects em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("residenciaDTO") ResidenciaCreateDTO residenciaDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "residencia/form";
        }

        residenciaService.create(residenciaDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Residência cadastrada com sucesso.");
    }

    /**
     * Abre o formulário de edição.
     *
     * @param id    identificador da residência
     * @param model modelo da view
     * @return nome da view {@code residencia/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, Model model) {
        ResidenciaResponseDTO residencia = residenciaService.findById(id);

        model.addAttribute("titulo", "Editar residência");
        model.addAttribute("residenciaDTO", new ResidenciaUpdateDTO(
                residencia.logradouro(),
                residencia.numero(),
                residencia.cep(),
                residencia.complemento(),
                residencia.cidade(),
                residencia.bairro(),
                residencia.numMoradores(),
                residencia.numTrabalhadores(),
                residencia.tipoOcupacao(),
                residencia.tipoResidencia()
        ));
        model.addAttribute("id", residencia.id());
        popularOpcoes(model);
        return "residencia/form";
    }

    /**
     * Atualiza uma residência.
     *
     * @param id                identificador da residência
     * @param residenciaDTO      dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular os selects em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("residenciaDTO") ResidenciaUpdateDTO residenciaDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        model.addAttribute("id", id);
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "residencia/form";
        }

        residenciaService.update(id, residenciaDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Residência atualizada com sucesso.");
    }

    /**
     * Apaga uma residência.
     *
     * <p>Residência com recebedor vinculado é recusada pela constraint de
     * integridade e chega ao advice como 409. Não há guard no service, ao
     * contrário de marca e tipo: a regra aqui é o banco, e a mensagem de 409
     * genérica é aceitável porque o usuário já está na tela do recebedor.</p>
     *
     * @param id                identificador da residência
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{id}")
    public String apagar(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        residenciaService.delete(id);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Residência apagada com sucesso.");
    }

    /**
     * Coloca no modelo as opções dos dois selects de enum.
     *
     * @param model modelo da view
     */
    private void popularOpcoes(Model model) {
        model.addAttribute("tiposOcupacao", Opcoes.deEnum(TipoOcupacao.class));
        model.addAttribute("tiposResidencia", Opcoes.deEnum(TipoResidencia.class));
    }
}
