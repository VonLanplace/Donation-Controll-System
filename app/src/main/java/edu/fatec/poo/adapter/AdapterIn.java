package edu.fatec.poo.adapter;

/**
 * Interface funcional responsável por adaptar dados que entram no sistema.
 * <p>
 * Atua na camada de entrada (Input Boundary), sendo utilizada para converter
 * objetos externos (como DTOs de requisição ou modelos de persistência) em
 * objetos internos de domínio ou casos de uso.
 * </p>
 *
 * @param <T> O tipo do objeto de origem (Entrada/Input).
 * @param <S> O tipo do objeto de destino transformado (Saída interna/Output).
 */
@FunctionalInterface
public interface AdapterIn<T, S> {

    /**
     * Realiza a conversão do objeto de entrada para o formato aceito internamente.
     *
     * @param object O objeto original vindo de uma fonte externa.
     * @return Uma instância do tipo S, processada para uso interno.
     * @throws IllegalArgumentException Caso o objeto de entrada seja nulo,
     *                                  esteja incompleto ou contenha dados inválidos.
     */
    public S toIn(T object) throws IllegalArgumentException;
}