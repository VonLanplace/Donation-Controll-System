package edu.fatec.poo.adapter;

/**
 * Interface funcional responsável por definir a estratégia de conversão (mapeamento)
 * de um objeto de entrada para um objeto de saída.
 * <p>
 * Geralmente utilizada em camadas de adaptação para converter entidades de domínio
 * em DTOs (Data Transfer Objects) ou vice-versa.
 * </p>
 *
 * @param <T> O tipo do objeto de origem (Entrada).
 * @param <S> O tipo do objeto de destino (Saída).
 */
public interface AdapterOut<T, S> {

    /**
     * Converte o objeto fornecido do tipo T para o tipo S.
     *
     * @param object O objeto a ser convertido.
     * @return Uma nova instância do tipo S representando o objeto convertido.
     * @throws IllegalArgumentException Se o objeto de entrada for nulo ou inválido
     *                                  para a conversão.
     */
    public S toOut(T object) throws IllegalArgumentException;
}