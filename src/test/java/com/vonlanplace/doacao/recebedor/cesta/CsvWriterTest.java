package com.vonlanplace.doacao.recebedor.cesta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Escrita CSV com escape e proteção contra fórmula.
 */
@Tag("unit")
@DisplayName("CsvWriter")
class CsvWriterTest {

    @ParameterizedTest(name = "campo [{0}] vira [{1}]")
    @MethodSource("campos")
    void escapaCampos(String entrada, String esperado) {
        assertThat(CsvWriter.escapar(entrada)).isEqualTo(esperado);
    }

    static Stream<Arguments> campos() {
        return Stream.of(
                Arguments.of("texto", "texto"),
                Arguments.of("=SUM(A1)", "'=SUM(A1)"),
                Arguments.of("+1", "'+1"),
                Arguments.of("-1", "'-1"),
                Arguments.of("@cmd", "'@cmd"),
                Arguments.of("\tvalor", "'\tvalor"),
                Arguments.of("\rvalor", "\"'\rvalor\""),
                Arguments.of("a;b", "\"a;b\""),
                Arguments.of("a\"b", "\"a\"\"b\""),
                Arguments.of("a\nb", "\"a\nb\""),
                Arguments.of("a\rb", "\"a\rb\""),
                Arguments.of("'=já-seguro", "'=já-seguro")
        );
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("nulo vira campo vazio")
    void nuloViraVazio(String entrada) {
        assertThat(CsvWriter.escapar(entrada)).isEmpty();
    }

    @Test
    @DisplayName("gera UTF-8 com BOM e CRLF")
    void geraBytes() {
        CsvWriter writer = new CsvWriter();
        writer.linha(List.of("nome", "valor"));
        writer.linha(List.of("Ana", "10,00"));

        byte[] bytes = writer.toBytes();
        String texto = new String(bytes, StandardCharsets.UTF_8);

        assertThat(bytes[0]).isEqualTo((byte) 0xEF);
        assertThat(bytes[1]).isEqualTo((byte) 0xBB);
        assertThat(bytes[2]).isEqualTo((byte) 0xBF);
        assertThat(texto).isEqualTo("\uFEFFnome;valor\r\nAna;10,00\r\n");
    }

    @Test
    @DisplayName("linha vazia produz apenas quebra")
    void linhaVazia() {
        CsvWriter writer = new CsvWriter();
        writer.linha(List.of());

        assertThat(new String(writer.toBytes(), StandardCharsets.UTF_8))
                .isEqualTo("\uFEFF\r\n");
    }
}
