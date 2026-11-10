package com.vonlanplace.doacao.recebedor.cesta;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Escritor CSV mínimo, com separador ponto e vírgula e UTF-8 com BOM.
 *
 * <p><strong>Por que não uma biblioteca.</strong> O projeto só precisa de um arquivo
 * simples; uma dependência nova não se paga. O cuidado que importa está aqui: escape
 * de aspas, separador e quebra de linha, além do prefixo contra injeção de fórmula no
 * Excel.</p>
 */
public final class CsvWriter {

    /** Separador usado pelo Excel em português. */
    private static final char SEPARADOR = ';';

    /** Quebra de linha CRLF, a mais compatível com Excel. */
    private static final String QUEBRA = "\r\n";

    /** Acumulador do arquivo. */
    private final StringBuilder conteudo = new StringBuilder();

    /**
     * Adiciona uma linha.
     *
     * @param valores valores da linha; {@code null} vira campo vazio
     */
    public void linha(List<String> valores) {
        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) {
                conteudo.append(SEPARADOR);
            }
            conteudo.append(escapar(valores.get(i)));
        }
        conteudo.append(QUEBRA);
    }

    /**
     * Devolve o CSV codificado em UTF-8, com BOM para o Excel reconhecer acentos.
     *
     * @return bytes do arquivo
     */
    public byte[] toBytes() {
        return ("\uFEFF" + conteudo).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Escapa um campo e neutraliza fórmulas.
     *
     * <p>Campos que começam com {@code =}, {@code +}, {@code -}, {@code @}, tab ou CR
     * são prefixados com apóstrofo. Sem isso, um comentário digitado pelo usuário
     * poderia ser interpretado como fórmula ao abrir o CSV no Excel.</p>
     *
     * @param valor valor original
     * @return campo seguro
     */
    static String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        String seguro = valor;
        if (!seguro.isEmpty() && (seguro.charAt(0) == '=' || seguro.charAt(0) == '+'
                || seguro.charAt(0) == '-' || seguro.charAt(0) == '@'
                || seguro.charAt(0) == '\t' || seguro.charAt(0) == '\r')) {
            seguro = "'" + seguro;
        }
        boolean precisaAspas = seguro.indexOf(SEPARADOR) >= 0
                || seguro.indexOf('"') >= 0
                || seguro.indexOf('\n') >= 0
                || seguro.indexOf('\r') >= 0;
        if (precisaAspas) {
            seguro = '"' + seguro.replace("\"", "\"\"") + '"';
        }
        return seguro;
    }
}
