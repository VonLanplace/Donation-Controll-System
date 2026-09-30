package com.vonlanplace.doacao.cesta;

import com.vonlanplace.doacao.recebedor.RecebedorMapper;
import org.mapstruct.*;

/**
 * Conversor entre {@link Cesta} e seus DTOs.
 *
 * <p>Segue o mesmo desenho de {@code RecebedorMapper}: os DTOs recebem
 * {@code recebedorId} e a entidade guarda a referência, então os dois métodos de escrita
 * ignoram {@code recebedor} e o {@code CestaService} resolve o vínculo pelo repositório.
 * MapStruct não associa UUID a entidade. O sentido inverso, entidade → DTO de edição, é
 * a ponte {@code source = "recebedor.id"}.</p>
 *
 * <p>A assimetria do {@code nullValuePropertyMappingStrategy.IGNORE}: ele vale para a
 * data de entrega, não para o recebedor, que é tratado fora do mapper.</p>
 */
@Mapper(componentModel = "spring", uses = {RecebedorMapper.class})
public interface CestaMapper {

    /**
     * Converte a entidade em DTO de resposta, aninhando o recebedor.
     *
     * @param cesta entidade de origem
     * @return DTO de resposta preenchido
     */
    CestaResponseDTO toResponseDTO(Cesta cesta);

    /**
     * Converte a entidade em DTO de edição, extraindo o id do recebedor.
     *
     * @param cesta entidade de origem
     * @return DTO de atualização preenchido
     */
    @Mapping(target = "recebedorId", source = "recebedor.id")
    CestaUpdateDTO toUpdateDTO(Cesta cesta);

    /**
     * Aplica o DTO sobre a entidade já carregada, sem tocar em {@code id} nem no
     * recebedor.
     *
     * @param dto  DTO de origem
     * @param cesta entidade de destino
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recebedor", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CestaUpdateDTO dto, @MappingTarget Cesta cesta);

    /**
     * Cria uma entidade nova a partir do DTO, sem id e sem recebedor.
     *
     * @param dto DTO de origem
     * @return entidade nova, ainda sem id e sem recebedor
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recebedor", ignore = true)
    Cesta toEntity(CestaCreateDTO dto);
}
