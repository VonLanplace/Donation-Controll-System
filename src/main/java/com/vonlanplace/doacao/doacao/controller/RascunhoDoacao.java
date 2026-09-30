package com.vonlanplace.doacao.doacao.controller;

import jakarta.servlet.http.HttpSession;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Rascunho da doação em edição, vivo só enquanto o usuário monta a doação.
 *
 * <p><strong>Guardado no {@link HttpSession} e não numa página escondida do
 * formulário.</strong> A doação é o único formulário do sistema com lista de
 * itens, e a lista é montada em passos: o usuário escolhe um produto, manda
 * "Adicionar produto", e a página volta com a linha nova embaixo das
 * anteriores. Sem JavaScript. Como cada "Adicionar" é um POST que
 * re-renderiza a mesma página, o estado acumulado precisa sobreviver entre
 * POSTs — é isso que a sessão dá, e é exatamente o papel que o flash de
 * redirect cumpre para a mensagem de sucesso no fim.</p>
 *
 * <p><strong>Por que sessão crua e não um bean {@code @SessionScope}:</strong>
 * o rascunho vive em atributo explícito da sessão, {@link #ATRIBUTO_SESSAO},
 * porque o bean de escopo de sessão não materializava o estado no
 * {@code MockHttpSession} dos testes do Spring Boot 4 — os testes de
 * integração do fluxo em dois POSTs exigiam uma passagem de estado segura no
 * servlet e no MockMvc ao mesmo tempo, e o atributo cru faz as duas coisas.</p>
 *
 * <p>O rascunho nasce a cada {@code GET /doacao/create} ou
 * {@code GET /doacao/editar/{id}} (via {@link #reiniciar()}), e é zerado no
 * {@code POST /doacao/form} que salva.</p>
 */
public class RascunhoDoacao implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Chave do atributo da sessão. Prefixo inusitado evita colisão com
     * qualquer atributo que o Spring Data ou o Spring MVC venha a gravar na
     * mesma sessão.
     */
    public static final String ATRIBUTO_SESSAO = "doacao.rascunho";

    private UUID id;

    private LocalDate data;

    private String nomeDoador;

    private UUID recebedorId;

    private UUID entrevistadorId;

    private final List<RascunhoLinha> linhas = new ArrayList<>();

    /**
     * Devolve o rascunho da sessão, criando-o no primeiro acesso.
     *
     * @param session sessão HTTP do usuário
     * @return o rascunho da sessão
     */
    public static RascunhoDoacao daSessao(HttpSession session) {
        Object atual = session.getAttribute(ATRIBUTO_SESSAO);
        if (atual instanceof RascunhoDoacao rascunho) {
            return rascunho;
        }
        RascunhoDoacao novo = new RascunhoDoacao();
        session.setAttribute(ATRIBUTO_SESSAO, novo);
        return novo;
    }

    /**
     * Zera o rascunho, em preparação para um formulário novo.
     */
    public void reiniciar() {
        id = null;
        data = null;
        nomeDoador = null;
        recebedorId = null;
        entrevistadorId = null;
        linhas.clear();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getNomeDoador() {
        return nomeDoador;
    }

    public void setNomeDoador(String nomeDoador) {
        this.nomeDoador = nomeDoador;
    }

    public UUID getRecebedorId() {
        return recebedorId;
    }

    public void setRecebedorId(UUID recebedorId) {
        this.recebedorId = recebedorId;
    }

    public UUID getEntrevistadorId() {
        return entrevistadorId;
    }

    public void setEntrevistadorId(UUID entrevistadorId) {
        this.entrevistadorId = entrevistadorId;
    }

    public List<RascunhoLinha> getLinhas() {
        return linhas;
    }

    /**
     * Uma linha do rascunho: produto, quantidade e validade.
     *
     * <p>Imutável de propósito: linhas entram pelo botão "Adicionar produto"
     * (que valida e monta a linha nova) e saem pelo botão "Remover" (índice).
     * Não há edição de linha existente, e um record que não pode ser alterado
     * deixa esse contrato visível.</p>
     *
     * @param produtoId    identificador do produto
     * @param quantidade   quantidade doada
     * @param dataValidade validade informada
     */
    public record RascunhoLinha(UUID produtoId, int quantidade, LocalDate dataValidade) implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;
    }
}