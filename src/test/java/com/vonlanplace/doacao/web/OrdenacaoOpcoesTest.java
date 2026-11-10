package com.vonlanplace.doacao.web;

import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ordenação de rótulos e montagem de opções de select.
 */
@Tag("unit")
@DisplayName("Ordenacao e Opcoes")
class OrdenacaoOpcoesTest {

    @ParameterizedTest(name = "[{0}] normaliza para [{1}]")
    @CsvSource({
            "Água, agua",
            "  Banana  , banana",
            "Café, cafe",
            "AÇÃO, acao",
            "123, 123"
    })
    void normalizaTexto(String entrada, String esperado) {
        assertThat(Ordenacao.normalizar(entrada)).isEqualTo(esperado);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("nulo normaliza para vazio")
    void normalizaNulo(String entrada) {
        assertThat(Ordenacao.normalizar(entrada)).isEmpty();
    }

    @Test
    @DisplayName("ordena valores ignorando acento e caixa")
    void ordenaValores() {
        assertThat(Opcoes.deValores(List.of("Café", "banana", "Água")))
                .extracting(Option::rotulo)
                .containsExactly("Água", "banana", "Café");
    }

    @Test
    @DisplayName("ordena enums pela descrição exibida")
    void ordenaEnums() {
        assertThat(Opcoes.deEnum(StatusRecebimento.class))
                .extracting(Option::valor, Option::rotulo)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("RECEBEU", "Recebeu"),
                        org.assertj.core.groups.Tuple.tuple("RECUSOU", "Recusou"));
    }

    @Test
    @DisplayName("enum sem getDescricao cai no name")
    void enumSemDescricao() {
        assertThat(Opcoes.deEnum(DayOfWeek.MONDAY))
                .isEqualTo(new Option("MONDAY", "MONDAY"));
    }

    @Test
    @DisplayName("ordena itens pelo rótulo, não pelo id")
    void ordenaItens() {
        List<Option> opcoes = Opcoes.deItens(
                List.of("b", "A", "c"),
                valor -> "id-" + valor,
                valor -> valor);

        assertThat(opcoes).extracting(Option::rotulo)
                .containsExactly("A", "b", "c");
    }
}
