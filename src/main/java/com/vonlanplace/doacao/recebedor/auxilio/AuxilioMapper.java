package com.vonlanplace.doacao.recebedor.auxilio;

import com.vonlanplace.doacao.recebedor.RecebedorMapper;
import org.mapstruct.*;

/**
 * Conversor entre {@link Auxilio} e seus DTOs.
 *
 * <p>Os DTOs recebem {@code recebedorId}
 * e a entidade guarda a referência, então os métodos de escrita ignoram
 * {@code recebedor} e o {@code AuxilioService} resolve o vínculo pelo
 * repositório. O sentido inverso é a ponte {@code source = "recebedor.id"}.</p>
 */
@Mapper(componentModel = "spring", uses = {RecebedorMapper.class})
public interface AuxilioMapper {

    /**
     * Converte a entidade em DTO de resposta, aninhando o recebedor.
     *
     * @param auxilio entidade de origem
     * @return DTO de resposta preenchido
     */
    AuxilioResponseDTO toResponseDTO(Auxilio auxilio);

    /**
     * Converte a entidade em DTO de edição, extraindo o id do recebedor.
     *
     * @param auxilio entidade de origem
     * @return DTO de atualização preenchido
     */
    @Mapping(target = "recebedorId", source = "recebedor.id")
    AuxilioUpdateDTO toUpdateDTO(Auxilio auxilio);

    /**
     * Aplica o DTO sobre a entidade já carregada, sem tocar em {@code id} nem no recebedor.
     *
     * @param dto      DTO de origem
     * @param auxilio entidade de destino
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recebedor", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(AuxilioUpdateDTO dto, @MappingTarget Auxilio auxilio);

    /**
     * Cria uma entidade nova a partir do DTO, sem id e sem recebedor.
     *
     * @param dto DTO de origem
     * @return entidade nova, ainda sem id e sem recebedor
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recebedor", ignore = true)
    Auxilio toEntity(AuxilioCreateDTO dto);
}