package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.entrevistador.EntrevistadorResponseDTO;
import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Representação de saída de uma {@link Doacao}.
 *
 * <p><strong>Os identificadores das relações não aparecem no lugar das entidades.</strong>
 * {@code recebedor} e {@code entrevistador} são os DTOs completos, não ids. Quando a
 * doação não tem vínculo, o campo vem {@code null} no JSON.</p>
 *
 * <p><strong>Este DTO aninha a residência inteira dentro de cada recebedor, e cada item
 * traz o produto completo com marca e tipo.</strong> Uma doação com vários itens repete o
 * recebedor a cada item. É o preço de não ter DTO intermediário, e vale ter isso em conta
 * antes de aumentar o tamanho da lista de produtos.</p>
 *
 * @param id           identificador da doação
 * @param data         data da doação
 * @param nomeDoador   nome de quem doou
 * @param recebedor    recebedor contemplado, ou {@code null}
 * @param entrevistador entrevistador responsável, ou {@code null}
 * @param produtos     itens doados
 */
public record DoacaoResponseDTO(
        UUID id,
        LocalDate data,
        String nomeDoador,
        RecebedorResponseDTO recebedor,
        EntrevistadorResponseDTO entrevistador,
        List<DoacaoProdutoResponseDTO> produtos
) {
}
