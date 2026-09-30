package com.vonlanplace.doacao.web;

import java.util.Comparator;
import java.util.Locale;

/**
 * Comparadores de ordenação compartilhados pelas páginas.
 *
 * <p><strong>Um só lugar porque o critério de ordenação é parte do
 * contrato.</strong> Se a listagem ordena por nome com acento tratado e a lista
 * de opções do formulário ordena sem, o mesmo conjunto de dados aparece em
 * duas ordens diferentes na mesma tela, e o usuário percebe como um bug.</p>
 */
public final class Ordenacao {

    /**
     * Construtor privado: classe de utilidade, só métodos estáticos.
     */
    private Ordenacao() {
    }

    /**
     * Ordena por rótulo ignorando acento e caixa.
     *
     * <p>"Água" vem antes de "Banana" e "banana" antes de "Café", como em
     * português. O fallback para o rótulo bruto desempata as duas opções
     * rotuladas igual, que acontece com nomes homônimos.</p>
     *
     * @return comparador por rótulo normalizado
     */
    public static Comparator<Option> porRotulo() {
        return Comparator
                .comparing((Option opcao) -> normalizar(opcao.rotulo()))
                .thenComparing(Option::rotulo, Comparator.nullsLast(String::compareTo));
    }

    /**
     * Colapsa acentos e caixa para comparar textos em português.
     *
     * @param texto texto a normalizar
     * @return texto sem acento e em minúscula, ou string vazia se nulo
     */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT).trim();
    }
}
