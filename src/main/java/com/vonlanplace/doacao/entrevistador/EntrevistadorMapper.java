package com.vonlanplace.doacao.entrevistador;

import org.mapstruct.Mapper;

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
}
