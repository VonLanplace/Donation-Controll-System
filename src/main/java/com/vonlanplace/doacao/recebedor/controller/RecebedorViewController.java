package com.vonlanplace.doacao.recebedor.controller;

import com.vonlanplace.doacao.recebedor.OrgaoEmissorRG;
import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;
import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioService;
import com.vonlanplace.doacao.recebedor.cesta.RecebedorCestaCsvService;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaService;
import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import com.vonlanplace.doacao.recebedor.dependente.DependenteService;
import com.vonlanplace.doacao.recebedor.form.RecebedorFormDTO;
import com.vonlanplace.doacao.recebedor.form.RecebedorFormService;
import com.vonlanplace.doacao.recebedor.form.RecebedorFormValidator;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaService;
import com.vonlanplace.doacao.web.Flash;
import com.vonlanplace.doacao.web.Opcoes;
import com.vonlanplace.doacao.web.PaginaView;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller Thymeleaf de recebedores, prefixo {@code /recebedor}.
 *
 * <p><strong>O cadastro é um formulário único.</strong> Dados pessoais,
 * residência, dependentes, auxílios e despesas são enviados juntos para
 * {@link RecebedorFormService}, que grava tudo na mesma transação. As antigas
 * telas independentes de residência, dependente, auxílio e despesa foram
 * removidas do recurso web; os endpoints REST continuam existindo.</p>
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
    private final DependenteService dependenteService;
    private final DespesaService despesaService;
    private final AuxilioService auxilioService;
    private final RecebimentoCestaService recebimentoCestaService;
    private final RecebedorCestaCsvService recebedorCestaCsvService;
    private final RecebedorFormService recebedorFormService;
    private final RecebedorFormValidator recebedorFormValidator;

    /**
     * Construtor com injeção de dependências.
     *
     * @param recebedorService          serviço de recebedores
     * @param dependenteService         serviço de dependentes, para o detalhe
     * @param despesaService            serviço de despesas, para o detalhe
     * @param auxilioService            serviço de auxílios, para o detalhe
     * @param recebimentoCestaService   serviço do histórico de cestas
     * @param recebedorCestaCsvService  serviço de exportação CSV
     * @param recebedorFormService      serviço do formulário unificado
     * @param recebedorFormValidator    validador das linhas dinâmicas
     */
    public RecebedorViewController(
            RecebedorService recebedorService,
            DependenteService dependenteService,
            DespesaService despesaService,
            AuxilioService auxilioService,
            RecebimentoCestaService recebimentoCestaService,
            RecebedorCestaCsvService recebedorCestaCsvService,
            RecebedorFormService recebedorFormService,
            RecebedorFormValidator recebedorFormValidator
    ) {
        this.recebedorService = recebedorService;
        this.dependenteService = dependenteService;
        this.despesaService = despesaService;
        this.auxilioService = auxilioService;
        this.recebimentoCestaService = recebimentoCestaService;
        this.recebedorCestaCsvService = recebedorCestaCsvService;
        this.recebedorFormService = recebedorFormService;
        this.recebedorFormValidator = recebedorFormValidator;
    }

    /**
     * Registra o validador das linhas dinâmicas do formulário.
     *
     * @param binder binder do formulário
     */
    @InitBinder("recebedorForm")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(recebedorFormValidator);
        binder.registerCustomEditor(int.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null || text.isBlank() ? 0 : Integer.parseInt(text.trim()));
            }
        });
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

        Map<UUID, StatusRecebimento> statusCestas = recebimentoCestaService.statusDoMes(
                pagina.getContent().stream().map(RecebedorResponseDTO::id).toList(),
                YearMonth.now()
        );

        model.addAttribute("titulo", "Recebedores");
        model.addAttribute("page", pagina);
        model.addAttribute("ordenacao", PaginaView.sortComoQuery(pageable));
        model.addAttribute("busca", busca == null ? "" : busca);
        model.addAttribute("statusCestas", statusCestas);
        model.addAttribute("mesAtual", YearMonth.now());
        return "recebedor/listar";
    }

    /**
     * Consulta um recebedor por CPF ou RG.
     *
     * @param documento CPF ou RG informado
     * @param redirectAttributes atributos do redirect
     * @param model modelo da view de seleção
     * @return redirect para o detalhe, ou a view {@code recebedor/selecao} quando houver
     *         mais de um RG com o mesmo número
     */
    @GetMapping("/consultar")
    public String consultar(
            @RequestParam(name = "documento", required = false) String documento,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        List<RecebedorResponseDTO> encontrados = recebedorService.findByDocumento(documento);
        if (encontrados.isEmpty()) {
            return Flash.erro(redirectAttributes, LISTAGEM, "Nenhum recebedor encontrado para o documento informado.");
        }
        if (encontrados.size() == 1) {
            return "redirect:/recebedor/detalhe/" + encontrados.get(0).id();
        }

        model.addAttribute("titulo", "Selecionar recebedor");
        model.addAttribute("documento", documento);
        model.addAttribute("recebedores", encontrados);
        return "recebedor/selecao";
    }

    /**
     * Exporta o relatório de cestas em CSV.
     *
     * @param inicio mês inicial no formato {@code AAAA-MM}
     * @param fim    mês final no formato {@code AAAA-MM}
     * @return arquivo CSV anexado à resposta
     */
    @GetMapping("/exportar.csv")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam(name = "inicio", required = false) String inicio,
            @RequestParam(name = "fim", required = false) String fim
    ) {
        YearMonth mesInicio = parseMes(inicio);
        YearMonth mesFim = parseMes(fim);
        if (mesInicio == null || mesFim == null || mesInicio.isAfter(mesFim)) {
            return ResponseEntity.badRequest().build();
        }

        byte[] csv = recebedorCestaCsvService.exportar(mesInicio, mesFim);
        ContentDisposition disposicao = ContentDisposition.attachment()
                .filename("cestas-" + mesInicio + "-a-" + mesFim + ".csv")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicao.toString())
                .contentType(new MediaType("text", "csv"))
                .body(csv);
    }

    /**
     * Gera o recibo em PDF de um recebimento de cesta.
     *
     * @param id identificador do recebimento
     * @return PDF anexado à resposta
     */
    @GetMapping("/cesta/pdf/{id}")
    public ResponseEntity<byte[]> reciboPdf(@PathVariable UUID id) {
        byte[] pdf = recebimentoCestaService.gerarReciboPdf(id);
        ContentDisposition disposicao = ContentDisposition.attachment()
                .filename("recibo-cesta-" + id + ".pdf")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicao.toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private YearMonth parseMes(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return YearMonth.parse(valor);
        } catch (DateTimeParseException ex) {
            return null;
        }
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
        RecebedorResponseDTO recebedor = recebedorService.findById(id);

        model.addAttribute("titulo", "Recebedor");
        model.addAttribute("recebedor", recebedor);
        model.addAttribute("dependentes", dependenteService.findByRecebedor(id));
        model.addAttribute("auxilios", auxilioService.findByRecebedor(id));
        model.addAttribute("despesas", despesaService.findByResidencia(recebedor.residencia().id()));
        model.addAttribute("totalDespesas", despesaService.somaPorResidencia(recebedor.residencia().id()));
        model.addAttribute("totalAuxilios", auxilioService.somaPorRecebedor(id));
        model.addAttribute("historicoCestas", recebimentoCestaService.historico(id));
        model.addAttribute("statusCestaAtual", recebimentoCestaService.statusAtual(id, YearMonth.now()));
        model.addAttribute("elegibilidadeCesta", recebimentoCestaService.verificarElegibilidade(id, YearMonth.now()));
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
        model.addAttribute("recebedorForm", new RecebedorFormDTO());
        popularOpcoes(model);
        return "recebedor/form";
    }

    /**
     * Cadastra um recebedor e todos os seus dados em uma única transação.
     *
     * @param recebedorForm      dados do formulário
     * @param resultado          erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model              modelo da view, para repopular os selects em caso de erro
     * @return redirect para o detalhe, ou a própria view em caso de erro
     */
    @PostMapping("/create")
    public String criar(
            @Valid @ModelAttribute("recebedorForm") RecebedorFormDTO recebedorForm,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "recebedor/form";
        }

        try {
            RecebedorResponseDTO recebedor = recebedorFormService.criar(recebedorForm);
            return Flash.sucesso(redirectAttributes,
                    "/recebedor/detalhe/" + recebedor.id(),
                    "Recebedor cadastrado com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            resultado.reject("erro.negocio", ex.getMessage());
            return "recebedor/form";
        }
    }

    /**
     * Abre o formulário de edição com todos os dados do recebedor.
     *
     * @param id    identificador do recebedor
     * @param model modelo da view
     * @return nome da view {@code recebedor/form}
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable UUID id, Model model) {
        model.addAttribute("titulo", "Editar recebedor");
        model.addAttribute("recebedorForm", recebedorFormService.carregarFormulario(id));
        model.addAttribute("id", id);
        model.addAttribute("elegibilidadeCesta",
                recebimentoCestaService.verificarElegibilidade(id, YearMonth.now()));
        popularOpcoes(model);
        return "recebedor/form";
    }

    /**
     * Atualiza o recebedor e sincroniza residência, dependentes, auxílios e despesas.
     *
     * @param id                 identificador do recebedor
     * @param recebedorForm      dados do formulário
     * @param resultado          erros de validação e de binding
     * @param redirectAttributes atributos do redirect
     * @param model              modelo da view, para repopular os selects em caso de erro
     * @return redirect para o detalhe, ou a própria view em caso de erro
     */
    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("recebedorForm") RecebedorFormDTO recebedorForm,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        model.addAttribute("id", id);
        popularOpcoes(model);
        if (resultado.hasErrors()) {
            return "recebedor/form";
        }

        try {
            recebedorFormService.atualizar(id, recebedorForm);
            return Flash.sucesso(redirectAttributes,
                    "/recebedor/detalhe/" + id,
                    "Recebedor atualizado com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            resultado.reject("erro.negocio", ex.getMessage());
            return "recebedor/form";
        }
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
     * @param model modelo da view
     */
    private void popularOpcoes(Model model) {
        model.addAttribute("emissores", Opcoes.deEnum(OrgaoEmissorRG.class));
        model.addAttribute("statusOpcoes", Opcoes.deEnum(StatusRecebimento.class));
        model.addAttribute("tiposOcupacao", Opcoes.deEnum(TipoOcupacao.class));
        model.addAttribute("tiposResidencia", Opcoes.deEnum(TipoResidencia.class));
    }
}
