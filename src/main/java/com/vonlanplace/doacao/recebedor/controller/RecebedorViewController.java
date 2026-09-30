package com.vonlanplace.doacao.recebedor.controller;

import com.vonlanplace.doacao.recebedor.OrgaoEmissorRG;
import com.vonlanplace.doacao.recebedor.RecebedorCreateDTO;
import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;
import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.recebedor.RecebedorUpdateDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaService;
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

import java.time.LocalDate;
import java.util.UUID;

/**
 * Controller Thymeleaf de recebedores, prefixo {@code /recebedor}.
 *
 * <p><strong>O formulário mais longo do sistema.</strong> Dezessete campos,
 * quatro deles booleanos, um por residência e um pela data. É também a
 * entidade central: produto não referencia recebedor, mas recebedor referencia
 * residência, doação referencia recebedor, e cesta referencia recebedor.</p>
 *
 * <p><strong>CPF é chave de negócio.</strong> O recebedor não tem um "identificador
 * público" no padrão da marca, mas o CPF é o que a tela de busca recebe e o que
 * {@code findByCpf} resolve. O id UUID continua sendo a chave das rotas, porque
 * CPF não existe antes do cadastro e é editável (ainda que quase nunca mude).</p>
 */
@Controller
@RequestMapping("/recebedor")
public class RecebedorViewController {

    /**
     * Quantidade de recebedores por página quando o cliente não informa {@code size}.
     */
    private static final int TAMANHO_PAGINA_PADRAO = 20;

    /**
     * URL da listagem, usada nos redirects e no fragmento de paginação.
     */
    private static final String LISTAGEM = "/recebedor/listar";

    private final RecebedorService recebedorService;
    private final ResidenciaService residenciaService;

    /**
     * Construtor com injeção de dependências.
     *
     * @param recebedorService  serviço de recebedores
     * @param residenciaService serviço de residências, para o select do formulário
     */
    public RecebedorViewController(
            RecebedorService recebedorService,
            ResidenciaService residenciaService
    ) {
        this.recebedorService = recebedorService;
        this.residenciaService = residenciaService;
    }

    /**
     * Lista recebedores, opcionalmente filtrando pelo nome.
     *
     * @param busca    trecho do nome completo, opcional
     * @param pageable paginação montada pelo Spring a partir da query string
     * @param model    modelo da view
     * @return nome da view {@code recebedor/listar}
     */
    @GetMapping({"", "/", "/listar"})
    public String listar(
            @RequestParam(name = "busca", required = false) String busca,
            @PageableDefault(size = TAMANHO_PAGINA_PADRAO, sort = "nomeCompleto",
                    direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<RecebedorResponseDTO> pagina = busca == null || busca.isBlank()
                ? recebedorService.findAll(pageable)
                : recebedorService.findByNomeCompleto(busca, pageable);

        model.addAttribute("titulo", "Recebedores");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        model.addAttribute("busca", busca == null ? "" : busca);
        return "recebedor/listar";
    }

    /**
     * Detalha um recebedor.
     *
     * @param id    identificador do recebedor
     * @param model modelo da view
     * @return nome da view {@code recebedor/detalhe}
     */
    @GetMapping("/detalhe/{id}")
    public String detalhe(@PathVariable UUID id, Model model) {
        model.addAttribute("titulo", "Recebedor");
        model.addAttribute("recebedor", recebedorService.findById(id));
        return "recebedor/detalhe";
    }

    /**
     * Abre o formulário de cadastro em branco.
     *
     * @param model modelo da view
     * @return nome da view {@code recebedor/form}
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("titulo", "Novo recebedor");
        model.addAttribute("recebedorDTO", new RecebedorCreateDTO(
                "", "", "", null, null, "", "", false, "", "", false, false,
                false, false, "", null
        ));
        popularOpcoes(model);
        return "recebedor/form";
    }

    /**
     * Cadastra um recebedor.
     *
     * @param recebedorDTO       dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular os selects em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("recebedorDTO") RecebedorCreateDTO recebedorDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "recebedor/form";
        }

        recebedorService.create(recebedorDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Recebedor cadastrado com sucesso.");
    }

    /**
     * Abre o formulário de edição.
     *
     * @param id    identificador do recebedor
     * @param model modelo da view
     * @return nome da view {@code recebedor/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, Model model) {
        RecebedorResponseDTO recebedor = recebedorService.findById(id);

        model.addAttribute("titulo", "Editar recebedor");
        model.addAttribute("recebedorDTO", new RecebedorUpdateDTO(
                recebedor.nomeCompleto(),
                recebedor.cpf(),
                recebedor.rgNum(),
                recebedor.rgEmissor(),
                recebedor.dataNascimento(),
                recebedor.telefonePessoal(),
                recebedor.telefoneRecado(),
                recebedor.leEscreve(),
                recebedor.email(),
                recebedor.pix(),
                recebedor.necessitaJuridico(),
                recebedor.necessitaPsicologo(),
                recebedor.recebeuCesta(),
                recebedor.parecerEntrevistador(),
                recebedor.comentarios(),
                recebedor.residencia().id()
        ));
        model.addAttribute("id", recebedor.id());
        popularOpcoes(model);
        return "recebedor/form";
    }

    /**
     * Atualiza um recebedor.
     *
     * @param id                identificador do recebedor
     * @param recebedorDTO       dados do formulário
     * @param resultado         erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model             modelo da view, para repopular os selects em caso de erro
     * @return redirect para a listagem, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("recebedorDTO") RecebedorUpdateDTO recebedorDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        model.addAttribute("id", id);
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "recebedor/form";
        }

        recebedorService.update(id, recebedorDTO);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Recebedor atualizado com sucesso.");
    }

    /**
     * Apaga um recebedor.
     *
     * <p>Recebedor com doação vinculada é recusado pela constraint de
     * integridade e chega ao advice como 409.</p>
     *
     * @param id                identificador do recebedor
     * @param redirectAttributes atributos do redirect
     * @return redirect para a listagem
     */
    @PostMapping("/apagar/{id}")
    public String apagar(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes
    ) {
        recebedorService.delete(id);
        return Flash.sucesso(redirectAttributes, LISTAGEM, "Recebedor apagado com sucesso.");
    }

    /**
     * Coloca no modelo as opções dos selects.
     *
     * <p>A lista de residências se apresenta como "{cidade} — {logradouro},
     * {numero}", porque o id de uma residência é um UUID que não significa nada
     * para o usuário e o logradouro sozinho não desambigua duas casas na mesma
     * rua.</p>
     *
     * @param model modelo da view
     */
    private void popularOpcoes(Model model) {
        model.addAttribute("emissores", Opcoes.deEnum(OrgaoEmissorRG.class));
        model.addAttribute("residencias", Opcoes.deItens(
                residenciaService.findAll(),
                r -> r.id().toString(),
                r -> r.cidade() + " — " + r.logradouro() + ", " + r.numero()
        ));
    }
}