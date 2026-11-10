package com.vonlanplace.doacao.recebedor.residencia;

import java.util.UUID;

/**
 * Representação de saída de uma {@link Residencia}.
 *
 * <p>Não inclui a lista de moradores, apesar de a entidade manter a relação. Como o
 * mapeamento é automático, a omissão é deliberada: aninhar a lista criaria recursão
 * (morador traz residência, que traz moradores) e, em
 * {@code RecebedorResponseDTO}, faria cada morador carregar os dados de todos os
 * vizinhos. Para ver os moradores, use {@code GET /api/recebedor/search?residenciaId=}.</p>
 *
 * @param id              identificador da residência
 * @param logradouro      logradouro
 * @param numero          número
 * @param cep             CEP sem hífen
 * @param complemento     complemento
 * @param cidade          cidade
 * @param bairro          bairro
 * @param numMoradores    quantidade de moradores
 * @param numTrabalhadores quantidade de trabalhadores
 * @param tipoOcupacao    perfil de ocupação
 * @param tipoResidencia  tipo de moradia
 */
public record ResidenciaResponseDTO(
        UUID id,
        String logradouro,
        String numero,
        String cep,
        String complemento,
        String cidade,
        String bairro,
        int numMoradores,
        int numTrabalhadores,
        TipoOcupacao tipoOcupacao,
        TipoResidencia tipoResidencia
) {
}
