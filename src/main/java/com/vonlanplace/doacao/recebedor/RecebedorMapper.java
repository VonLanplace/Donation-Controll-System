package com.vonlanplace.doacao.recebedor;

import com.vonlanplace.doacao.recebedor.residencia.ResidenciaMapper;
import org.mapstruct.*;

/**
 * Conversor entre {@link Recebedor} e seus DTOs.
 *
 * <p><strong>Por que a residência é resolvida à mão no serviço.</strong> Os DTOs de
 * entrada recebem {@code residenciaId} (UUID), mas a entidade guarda a referência
 * {@code Residencia}. MapStruct não associa UUID a entidade, então
 * {@code updateEntityFromDTO} e {@code toEntity} ignoram a residência e o serviço a
 * busca pelo repositório antes de salvar. Já a volta
 * {@code toResponseDTO → toUpdateDTO} é a ponte simples
 * {@code source = "residencia.id"}.</p>
 *
 * <p>{@code doacoes} é sempre ignorado: a coleção pertence a {@code Doacao}, que é o
 * lado dono da relação.</p>
 */
@Mapper(componentModel = "spring", uses = {ResidenciaMapper.class})
public interface RecebedorMapper {

    /**
     * Converte a entidade em DTO de resposta, aninhando a residência e os demais
     * moradores dela.
     *
     * @param recebedor entidade de origem
     * @return DTO de resposta preenchido
     */
    RecebedorResponseDTO toResponseDTO(Recebedor recebedor);

    /**
     * Converte a entidade em DTO de edição, extraindo o id da residência.
     *
     * @param recebedor entidade de origem
     * @return DTO de atualização preenchido
     */
    @Mapping(target = "residenciaId", source = "residencia.id")
    RecebedorUpdateDTO toUpdateDTO(Recebedor recebedor);

    /**
     * Aplica o DTO sobre a entidade já carregada, sem tocar em {@code id},
     * {@code residencia} nem {@code doacoes}.
     *
     * @param dto       DTO de origem
     * @param recebedor entidade de destino
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "residencia", ignore = true)
    @Mapping(target = "doacoes", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(RecebedorUpdateDTO dto, @MappingTarget Recebedor recebedor);

    /**
     * Cria uma entidade nova a partir do DTO, sem id e sem residência.
     *
     * @param dto DTO de origem
     * @return entidade nova, ainda sem id e sem residência
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "residencia", ignore = true)
    @Mapping(target = "doacoes", ignore = true)
    Recebedor toEntity(RecebedorCreateDTO dto);
}
