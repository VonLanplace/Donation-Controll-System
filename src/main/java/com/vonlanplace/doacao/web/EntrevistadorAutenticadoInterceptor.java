package com.vonlanplace.doacao.web;

import com.vonlanplace.doacao.entrevistador.EntrevistadorRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Fecha as rotas web para quem não tem entrevistador logado na sessão.
 *
 * <p><strong>É a imagem espelhada do {@link LoginController}:</strong> o login
 * grava o atributo na sessão, este interceptor só confere que ele está lá.
 * Nenhum filtro do Spring Security participa — a autorização é esta classe e
 * nada mais.</p>
 *
 * <p><strong>Liberação por bootstrap.</strong> Antes do primeiro entrevistador
 * existir, não há credencial que abra sessão: o sistema estaria trancado para
 * sempre. Por isso {@code /entrevistador/create} escapa do bloqueio enquanto
 * {@code count() == 0}; cadastrado o primeiro usuário, a rota volta a exigir
 * login. A contagem a cada requisição é barata no volume deste sistema.</p>
 *
 * <p><strong>Só olha o caminho exato.</strong> {@code /entrevistador/create}
 * liberado com {@code count() == 0} mantém {@code /entrevistador/editar/...} e
 * a listagem protegidos mesmo sem usuário: o cadastro é o único que faz
 * sentido sem sessão.</p>
 */
@Component
public class EntrevistadorAutenticadoInterceptor implements HandlerInterceptor {

    private final EntrevistadorRepository entrevistadorRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param entrevistadorRepository repositório usado na checagem de bootstrap
     */
    public EntrevistadorAutenticadoInterceptor(EntrevistadorRepository entrevistadorRepository) {
        this.entrevistadorRepository = entrevistadorRepository;
    }

    /**
     * Bloqueia a requisição quando não há sessão logada.
     *
     * @param request  requisição
     * @param response resposta
     * @param handler  handler da rota
     * @return {@code true} se pode seguir; {@code false} após o redirect
     * @throws IOException se o redirect falhar
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        if (primeiroCadastroLiberado(request)) {
            return true;
        }

        HttpSession sessao = request.getSession(false);
        if (sessao != null && sessao.getAttribute(LoginController.ATRIBUTO_SESSAO) != null) {
            return true;
        }

        response.sendRedirect("/login");
        return false;
    }

    /**
     * Decide se a rota é o cadastro do primeiro entrevistador.
     *
     * @param request requisição
     * @return {@code true} quando não há nenhum entrevistador cadastrado
     */
    private boolean primeiroCadastroLiberado(HttpServletRequest request) {
        return "/entrevistador/create".equals(request.getRequestURI())
                && entrevistadorRepository.count() == 0;
    }
}