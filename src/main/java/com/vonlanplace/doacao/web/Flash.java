package com.vonlanplace.doacao.web;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Mensagens de retorno para as páginas de escrita.
 *
 * <p><strong>Toda escrita responde redirect e deixa a mensagem no flash.</strong>
 * É o motivo de esta classe existir. Se o controller renderizar a view direto
 * depois do POST, o F5 reenvia o formulário e cria um registro duplicado; se
 * redirectar sem flash, o redirect decai na listagem sem nenhum aviso e o
 * usuário não sabe se salvou.</p>
 *
 * <p>O par {@code mensagem} e {@code tipo} é o que o fragmento
 * {@code fragments/flash.html} consome.</p>
 */
public final class Flash {

    /**
     * Construtor privado: classe de utilidade, só métodos estáticos.
     */
    private Flash() {
    }

    /**
     * Registra uma mensagem de sucesso e devolve o redirect para a listagem.
     *
     * @param redirectAttributes atributos do redirect
     * @param listagem           URL da listagem, sem o prefixo {@code redirect:}
     * @param mensagem           texto exibido
     * @return o nome da view de redirect
     */
    public static String sucesso(RedirectAttributes redirectAttributes, String listagem, String mensagem) {
        return registrar(redirectAttributes, listagem, mensagem, "sucesso");
    }

    /**
     * Registra uma mensagem de erro de negócio e devolve o redirect para a listagem.
     *
     * <p>Usada quando o POST passou pela validação de formato mas foi recusado
     * pela regra de negócio, como apagar uma marca que ainda tem produto.</p>
     *
     * @param redirectAttributes atributos do redirect
     * @param listagem           URL da listagem
     * @param mensagem           texto exibido
     * @return o nome da view de redirect
     */
    public static String erro(RedirectAttributes redirectAttributes, String listagem, String mensagem) {
        return registrar(redirectAttributes, listagem, mensagem, "erro");
    }

    /**
     * Registra uma mensagem informativa e devolve o redirect.
     *
     * @param redirectAttributes atributos do redirect
     * @param listagem           URL da listagem
     * @param mensagem           texto exibido
     * @return o nome da view de redirect
     */
    public static String info(RedirectAttributes redirectAttributes, String listagem, String mensagem) {
        return registrar(redirectAttributes, listagem, mensagem, "info");
    }

    /**
     * Guarda a mensagem no flash e monta o redirect.
     *
     * @param redirectAttributes atributos do redirect
     * @param listagem           URL de destino
     * @param mensagem           texto exibido
     * @param tipo               sufixo da classe CSS: sucesso, erro ou info
     * @return o nome da view de redirect
     */
    private static String registrar(
            RedirectAttributes redirectAttributes,
            String listagem,
            String mensagem,
            String tipo
    ) {
        redirectAttributes.addFlashAttribute("mensagem", mensagem);
        redirectAttributes.addFlashAttribute("tipo", tipo);
        return "redirect:" + listagem;
    }
}
