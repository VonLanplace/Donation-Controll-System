package com.vonlanplace.doacao.web;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Apoio comum às páginas de listagem.
 *
 * <p><strong>Existe por causa de dois erros que só aparecem em runtime.</strong>
 * O primeiro é iterar sobre a {@link Page} com {@code #lists}: {@code Page} não
 * é {@code List}, e a conversão de tipo do SpEL estoura antes do
 * {@code th:each} começar, o que dá 500 em página que "tem dados". O segundo é
 * interpolar {@code page.sort} num link: o resultado é
 * {@code "codigoBarras: ASC"}, que não é um valor válido de {@code ?sort=}, e a
 * ordenação se perde ao trocar de página.</p>
 *
 * <p>As duas coisas aqui são o porquê do fragmento {@code pagination} receber uma
 * {@code String} e não a {@code Page}.</p>
 */
public final class PaginaView {

    /**
     * Construtor privado: classe de utilidade, só métodos estáticos.
     */
    private PaginaView() {
    }

    /**
     * Converte a ordenação da requisição no formato de query param.
     *
     * @param pageable paginação recebida
     * @return ordenação no formato {@code propriedade,direcao[,propriedade,direcao]}
     */
    public static String sortComoQuery(Pageable pageable) {
        return pageable.getSort().stream()
                .map(ordem -> ordem.getProperty() + "," + ordem.getDirection().name().toLowerCase())
                .collect(Collectors.joining(","));
    }

    /**
     * Devolve a lista de conteúdo da página, nunca a {@link Page} em si.
     *
     * @param pagina página paginada
     * @param <T>    tipo do conteúdo
     * @return a lista de itens da página atual
     */
    public static <T> List<T> conteudo(Page<T> pagina) {
        return pagina.getContent();
    }

    /**
     * Diz se a página tem itens, para o caso de a listagem vir vazia.
     *
     * @param pagina página paginada
     * @return {@code true} quando há ao menos um item
     */
    public static boolean vazia(Page<?> pagina) {
        return pagina.getTotalElements() == 0;
    }
}
