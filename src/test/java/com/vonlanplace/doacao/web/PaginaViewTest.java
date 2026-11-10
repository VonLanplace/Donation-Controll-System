package com.vonlanplace.doacao.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Apoio de paginação das views.
 */
@Tag("unit")
@DisplayName("PaginaView")
class PaginaViewTest {

    @Test
    @DisplayName("converte sort em query string")
    void sortComoQuery() {
        PageRequest pageable = PageRequest.of(
                0, 20, Sort.by(Sort.Order.asc("nome"), Sort.Order.desc("cpf")));

        assertThat(PaginaView.sortComoQuery(pageable)).isEqualTo("nome,asc,cpf,desc");
    }

    @Test
    @DisplayName("sort vazio vira string vazia")
    void sortVazio() {
        assertThat(PaginaView.sortComoQuery(PageRequest.of(0, 20))).isEmpty();
    }

    @Test
    @DisplayName("conteúdo devolve a lista da página")
    void conteudo() {
        Page<String> pagina = new PageImpl<>(List.of("a", "b"));

        assertThat(PaginaView.conteudo(pagina)).containsExactly("a", "b");
    }

    @Test
    @DisplayName("vazia reflete total de elementos")
    void vazia() {
        assertThat(PaginaView.vazia(new PageImpl<>(List.of()))).isTrue();
        assertThat(PaginaView.vazia(new PageImpl<>(List.of("a")))).isFalse();
    }
}
