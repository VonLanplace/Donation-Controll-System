package com.vonlanplace.doacao.recebedor.residencia.despesa;

import com.vonlanplace.doacao.recebedor.residencia.ResidenciaMapper;
import org.mapstruct.*;

/**
 * Conversor entre {@link Despesa} e seus DTOs.
 *
 * <p>Os DTOs recebem {@code residenciaId}
 * e a entidade guarda a referência, então os métodos de escrita ignoram
 * {@code residencia} e o {@code DespesaService} resolve o vínculo pelo
 * repositório. O sentido inverso é a ponte {@code source = "residencia.id"}.</p>
 */
@Mapper(componentModel = "spring", uses = {ResidenciaMapper.class})
public interface DespesaMapper {

    /**
     * Converte a entidade em DTO de resposta, aninhando a residência.
     *
     * @param despesa entidade de origem
     * @return DTO de resposta preenchido
     */
    DespesaResponseDTO toResponseDTO(Despesa despesa);

    /**
     * Converte a entidade em DTO de edição, extraindo o id da residência.
     *
     * @param despesa entidade de origem
     * @return DTO de atualização preenchido
     */
    @Mapping(target = "residenciaId", source = "residencia.id")
    DespesaUpdateDTO toUpdateDTO(Despesa despesa);

    /**
     * Aplica o DTO sobre a entidade já carregada, sem tocar em {@code id} nem na residência.
     *
     * @param dto     DTO de origem
     * @param despesa entidade de destino
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "residencia", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(DespesaUpdateDTO dto, @MappingTarget Despesa despesa);

    /**
     * Cria uma entidade nova a partir do DTO, sem id e sem residência.
     *
     * @param dto DTO de origem
     * @return entidade nova, ainda sem id e sem residência
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "residencia", ignore = true)
    Despesa toEntity(DespesaCreateDTO dto);
}