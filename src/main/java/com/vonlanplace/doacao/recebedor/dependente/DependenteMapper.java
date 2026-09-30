package com.vonlanplace.doacao.recebedor.dependente;

import com.vonlanplace.doacao.recebedor.RecebedorMapper;
import org.mapstruct.*;

/**
 * Conversor entre {@link Dependente} e seus DTOs.
 *
 * <p>Segue o desenho de {@code CestaMapper}: os DTOs recebem {@code recebedorId}
 * e a entidade guarda a referência, então os dois métodos de escrita ignoram
 * {@code recebedor} e o {@code DependenteService} resolve o vínculo pelo
 * repositório. O sentido inverso é a ponte {@code source = "recebedor.id"}.</p>
 */
@Mapper(componentModel = "spring", uses = {RecebedorMapper.class})
public interface DependenteMapper {

    /**
     * Converte a entidade em DTO de resposta, aninhando o recebedor.
     *
     * @param dependente entidade de origem
     * @return DTO de resposta preenchido
     */
    DependenteResponseDTO toResponseDTO(Dependente dependente);

    /**
     * Converte a entidade em DTO de edição, extraindo o id do recebedor.
     *
     * @param dependente entidade de origem
     * @return DTO de atualização preenchido
     */
    @Mapping(target = "recebedorId", source = "recebedor.id")
    DependenteUpdateDTO toUpdateDTO(Dependente dependente);

    /**
     * Aplica o DTO sobre a entidade já carregada, sem tocar em {@code id} nem no recebedor.
     *
     * @param dto        DTO de origem
     * @param dependente entidade de destino
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recebedor", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(DependenteUpdateDTO dto, @MappingTarget Dependente dependente);

    /**
     * Cria uma entidade nova a partir do DTO, sem id e sem recebedor.
     *
     * @param dto DTO de origem
     * @return entidade nova, ainda sem id e sem recebedor
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recebedor", ignore = true)
    Dependente toEntity(DependenteCreateDTO dto);
}