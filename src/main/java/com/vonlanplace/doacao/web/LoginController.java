package com.vonlanplace.doacao.web;

import com.vonlanplace.doacao.entrevistador.EntrevistadorRepository;
import com.vonlanplace.doacao.entrevistador.EntrevistadorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Página de login baseada em sessão.
 *
 * <p><strong>A sessão é o mecanismo, não o Spring Security.</strong> Nenhum
 * filtro de segurança intercepta as requisições: quem fecha as rotas web é o
 * {@link EntrevistadorAutenticadoInterceptor}, e o que ele procura na sessão é
 * este atributo. Entrar grava o entrevistador logado na {@link HttpSession};
 * sair invalida a sessão inteira.</p>
 *
 * <p><strong>Não faz parte do {@code ViewExceptionHandler}.</strong> O advice
 * de view trata {@code EntityNotFoundException} das telas de recurso; login não
 * resolve recurso por id e não levanta essa exceção. Deixá-lo fora da lista
 * também mantém o {@code ViewExceptionHandlerEscopoTest} coerente, que exige
 * que todo controller citado em {@code assignableTypes} seja um
 * {@code *ViewController} ou a {@code HomeController}.</p>
 */
@Controller
public class LoginController {

    /**
     * Chave do entrevistador logado na sessão. É o que o interceptor procura e
     * o que o header consulta via {@code session.entrevistadorLogado}.
     */
    public static final String ATRIBUTO_SESSAO = "entrevistadorLogado";

    private final AutenticacaoService autenticacaoService;
    private final EntrevistadorRepository entrevistadorRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param autenticacaoService      validação de credenciais
     * @param entrevistadorRepository  consultado para saber se o primeiro
     *                                 entrevistador ainda precisa ser criado
     */
    public LoginController(
            AutenticacaoService autenticacaoService,
            EntrevistadorRepository entrevistadorRepository
    ) {
        this.autenticacaoService = autenticacaoService;
        this.entrevistadorRepository = entrevistadorRepository;
    }

    /**
     * Abre o formulário de login.
     *
     * <p>Sem nenhum entrevistador no banco, o formulário exibe o link de
     * {@code /entrevistador/create}. O interceptor libera essa rota só enquanto
     * {@code count() == 0}: é a porta de entrada que impede o sistema de
     * começar sem credenciais.</p>
     *
     * @param model modelo da view
     * @return nome da view {@code login/form}
     */
    @GetMapping("/login")
    public String login(Model model) {
        return modeloLogin(model);
    }

    /**
     * Valida as credenciais e abre a sessão logada.
     *
     * @param credenciais  e-mail e senha do formulário
     * @param resultado    erros de validação
     * @param sessao       sessão HTTP, onde o logado é gravado
     * @param model        modelo da view
     * @return redirect para a home, ou a própria view em caso de erro
     */
    @PostMapping("/login")
    public String entrar(
            @Valid @ModelAttribute("credenciaisDTO") CredenciaisDTO credenciais,
            BindingResult resultado,
            HttpSession sessao,
            Model model
    ) {
        if (resultado.hasErrors()) {
            return "login/form";
        }

        try {
            EntrevistadorResponseDTO logado = autenticacaoService.autenticar(
                    credenciais.email(),
                    credenciais.senha()
            );
            sessao.setAttribute(ATRIBUTO_SESSAO, logado);
            return "redirect:/";
        } catch (CredenciaisInvalidasException e) {
            model.addAttribute("erro", e.getMessage());
            return "login/form";
        }
    }

    /**
     * Encerra a sessão.
     *
     * <p><strong>POST, não GET.</strong> Um link comum permitiria expulsar a
     * própria sessão por engano (antecipação de teclado, pré-carregamento de
     * link). Ação que muda estado pede método mutável.</p>
     *
     * @param request requisição, de onde a sessão é obtida
     * @return redirect para o login
     */
    @PostMapping("/logout")
    public String sair(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        if (sessao != null) {
            sessao.invalidate();
        }
        return "redirect:/login";
    }

    /**
     * Popula o modelo do formulário de login.
     *
     * @param model modelo da view
     * @return nome da view {@code login/form}
     */
    private String modeloLogin(Model model) {
        model.addAttribute("titulo", "Entrar");
        model.addAttribute("credenciaisDTO", new CredenciaisDTO("", ""));
        model.addAttribute("podeCriarPrimeiro", entrevistadorRepository.count() == 0);
        return "login/form";
    }
}