package com.vonlanplace.doacao.recebedor.residencia;

import org.mapstruct.*;

/**
 * Conversor entre {@link Residencia} e seus DTOs.
 *
 * <p>Mapeamento 1:1 de campos, sem ponte nem DTO intermediário. O que exige atenção é o
 * {@code recebedores}: a entidade tem a coleção, o DTO de resposta não tem, e o DTO de
 * atualização também não. Por isso o campo é ignorado explicitamente nas duas operações
 * de escrita — sem o {@code ignore}, o MapStruct geraria aviso de propriedade não
 * mapeada ou erro de compilação.</p>
 *
 * <p>Os residents são gravados só pelo lado do recebedor; esta classe nunca cria ou
 * remove moradores.</p>
 */
@Mapper(componentModel = "spring")
public interface ResidenciaMapper {

    /**
     * Converte a entidade em DTO de resposta.
     *
     * @param residencia entidade de origem
     * @return DTO de resposta preenchido
     */
    ResidenciaResponseDTO toResponseDTO(Residencia residencia);

    /**
     * Converte a entidade em DTO de edição.
     *
     * @param residencia entidade de origem
     * @return DTO de atualização preenchido
     */
    ResidenciaUpdateDTO toUpdateDTO(Residencia residencia);

    /**
     * Aplica o DTO sobre a entidade já carregada, sem tocar em {@code id} nem na
     * coleção de moradores.
     *
     * @param dto        DTO de origem
     * @param residencia entidade de destino
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recebedores", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ResidenciaUpdateDTO dto, @MappingTarget Residencia residencia);

    /**
     * Cria uma entidade nova a partir do DTO.
     *
     * @param dto DTO de origem
     * @return entidade nova, ainda sem id e sem moradores
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recebedores", ignore = true)
    Residencia toEntity(ResidenciaCreateDTO dto);
}
