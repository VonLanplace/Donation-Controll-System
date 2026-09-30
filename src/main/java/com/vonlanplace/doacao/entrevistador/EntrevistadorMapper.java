package com.vonlanplace.doacao.entrevistador;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Conversor de leitura de {@link Entrevistador} para
 * {@link EntrevistadorResponseDTO}.
 *
 * <p><strong>Conversão segura por omissão, não por configuração.</strong> Não existe
 * {@code @Mapping(target = "senhaHash", ignore = true)} aqui, e não deveria: o DTO de
 * destino não tem o campo, então o MapStruct nunca tem o que preencher. A proteção
 * depende de {@link EntrevistadorResponseDTO} continuar sem
 * {@code senhaHash}; acrescentar o campo ao record vazaria o hash em silêncio, sem erro
 * de compilação.</p>
 */
@Mapper(componentModel = "spring")
public interface EntrevistadorMapper {

    /**
     * Converte a entidade em DTO de resposta, sem a senha.
     *
     * @param entrevistador entidade de origem
     * @return DTO de resposta preenchido
     */
    EntrevistadorResponseDTO toResponseDTO(Entrevistador entrevistador);

    /**
     * Converte o DTO de cadastro em entidade, sem a senha.
     *
     * <p>{@code senha} é ignorado de propósito, e o {@code ignore} é explícito
     * porque aqui os dois lados têm nomes diferentes: a entidade tem
     * {@code senhaHash} e o DTO tem {@code senha}. Sem o {@code ignore} o
     * MapStructtentaria preencher {@code senhaHash} com {@code senha}, que é
     * exatamente o que este mapper não deve fazer — a codificação é do
     * service.</p>
     *
     * @param createDTO DTO de cadastro
     * @return entidade nova, sem hash preenchido
     */
    @Mapping(target = "senhaHash", ignore = true)
    @Mapping(target = "id", ignore = true)
    Entrevistador toEntity(EntrevistadorCreateDTO createDTO);

    /**
     * Atualiza a entidade com os dados do DTO de edição.
     *
     * <p>Mesma razão do {@link #toEntity}: {@code senha} não pode virar
     * {@code senhaHash}. O service decide, depois, se toca no hash.</p>
     *
     * @param updateDTO     DTO de edição
     * @param entrevistador entidade de destino, já carregada pelo service
     */
    @Mapping(target = "senhaHash", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(EntrevistadorUpdateDTO updateDTO, @MappingTarget Entrevistador entrevistador);
}
