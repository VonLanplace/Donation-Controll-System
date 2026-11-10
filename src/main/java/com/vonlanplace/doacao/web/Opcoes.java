package com.vonlanplace.doacao.web;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Construtor das opções de {@code <select>}.
 *
 * <p><strong>Todos os valores de select do projeto passam por aqui.</strong> A
 * alternativa é escrever a lista à mão em cada controller, e o resultado
 * diverge: um controller ordena por nome, outro por id, outro esquece o item
 * "Selecione…". Aqui a ordem é sempre alfabética por rótulo, que é o que o
 * usuário espera ao procurar um item numa lista.</p>
 */
public final class Opcoes {

    /**
     * Construtor privado: classe de utilidade, só métodos estáticos.
     */
    private Opcoes() {
    }

    /**
     * Opções a partir de uma lista de valores, usando o próprio valor como rótulo.
     *
     * @param valores valores a listar
     * @return opções ordenadas alfabeticamente
     */
    public static List<Option> deValores(List<String> valores) {
        return valores.stream()
                .map(valor -> new Option(valor, valor))
                .sorted(Ordenacao.porRotulo())
                .toList();
    }

    /**
     * Opções a partir de um stream de valores, com o valor como rótulo.
     *
     * @param valores stream de valores
     * @return opções ordenadas alfabeticamente
     */
    public static List<Option> deValores(Stream<String> valores) {
        return deValores(valores.toList());
    }

    /**
     * Opções de um enum: grava {@code name()} e exibe a descrição em português.
     *
     * <p>A descrição vem de um método {@code getDescricao()} declarado no enum. Se
     * o enum não tiver, cai no {@code name()}, que pelo menos funciona.</p>
     *
     * @param tipo   classe do enum
     * @param <T>    tipo do enum
     * @return opções ordenadas alfabeticamente pelo rótulo exibido
     */
    public static <T extends Enum<T>> List<Option> deEnum(Class<T> tipo) {
        return Stream.of(tipo.getEnumConstants())
                .map(Opcoes::deEnum)
                .sorted(Ordenacao.porRotulo())
                .toList();
    }

    /**
     * Converte um enum isolado em {@link Option}.
     *
     * @param constante constante do enum
     * @param <T>       tipo do enum
     * @return opção com o {@code name()} como valor
     */
    public static <T extends Enum<T>> Option deEnum(T constante) {
        return new Option(constante.name(), descricaoDe(constante));
    }

    /**
     * Opções de uma lista de entidades, exibindo um campo e gravando o id.
     *
     * <p>Ordena pelo rótulo, não pelo id: uma lista de recebedores ordenada por
     * UUID é impossível de usar.</p>
     *
     * @param itens  lista de entidades
     * @param id     extrai o valor gravado
     * @param rotulo extrai o texto exibido
     * @param <T>    tipo da entidade
     * @return opções ordenadas alfabeticamente
     */
    public static <T> List<Option> deItens(
            List<T> itens,
            Function<T, String> id,
            Function<T, String> rotulo
    ) {
        return itens.stream()
                .map(item -> new Option(id.apply(item), rotulo.apply(item)))
                .sorted(Ordenacao.porRotulo())
                .toList();
    }

    /**
     * Lê a descrição em português de um enum, tolerando enum sem esse método.
     *
     * @param constante constante a descrever
     * @return a descrição, ou o nome da constante se não houver descrição
     */
    private static String descricaoDe(Enum<?> constante) {
        try {
            return (String) constante.getClass()
                    .getMethod("getDescricao")
                    .invoke(constante);
        } catch (ReflectiveOperationException | ClassCastException semMetodo) {
            return constante.name();
        }
    }
}
