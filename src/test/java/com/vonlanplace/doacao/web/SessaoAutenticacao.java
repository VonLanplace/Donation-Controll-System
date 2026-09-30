package com.vonlanplace.doacao.web;

import com.vonlanplace.doacao.entrevistador.EntrevistadorResponseDTO;
import org.springframework.mock.web.MockHttpSession;

import java.util.UUID;

/**
 * Sessão autenticada para os testes de renderização de página.
 *
 * <p><strong>Existe porque o interceptor de autenticação fechou as rotas
 * web.</strong> Antes da fase de login, um teste web era um
 * {@code mockMvc.perform(get("/x"))} sem nada além da URL; agora a mesma
 * chamada devolve redirect para {@code /login}. Cada teste de renderização
 * precisa entrar com uma {@link MockHttpSession} cujo atributo de logado está
 * presente — que é exatamente o que estes testes não querem exercitar.</p>
 *
 * <p><strong>O DPTO é fabricado, não persistido.</strong> O interceptor confere
 * apenas a presença do atributo na sessão; nenhum teste de renderização precisa
 * que o entrevistador exista no banco. O fluxo real de credenciais fica com o
 * {@code AutenticacaoTest}, que usa BCrypt de verdade.</p>
 */
public final class SessaoAutenticacao {

    /**
     * Impossível de instanciar: classe de utilitário.
     */
    private SessaoAutenticacao() {
    }

    /**
     * Fabricar uma sessão com um entrevistador logado.
     *
     * @return sessão HTTP com {@code entrevistadorLogado} preenchido
     */
    public static MockHttpSession logada() {
        MockHttpSession sessao = new MockHttpSession();
        sessao.setAttribute(
                LoginController.ATRIBUTO_SESSAO,
                new EntrevistadorResponseDTO(
                        UUID.randomUUID(),
                        "Entrevistador de Teste",
                        "teste@exemplo.com"
                )
        );
        return sessao;
    }
}