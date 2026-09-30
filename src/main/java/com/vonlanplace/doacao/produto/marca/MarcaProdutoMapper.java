package com.vonlanplace.doacao.produto.marca;

import org.mapstruct.*;

/**
 * Conversor entre {@link MarcaProduto} e seus DTOs.
 *
 * <p>Conversão direta de um único campo {@code nome}. Os métodos que escrevem na
 * entidade ignoram {@code id} e {@code produtos}: o id é gerado pelo banco e a coleção
 * é mantida pelo lado dono da relação, {@code Produto.marcaProduto}.</p>
 */
@Mapper(componentModel = "spring")
public interface MarcaProdutoMapper {

    /**
     * Converte a entidade em DTO de resposta.
     *
     * @param marcaProduto entidade de origem
     * @return DTO contendo apenas o nome
     */
    MarcaProdutoResponseDTO toResponseDTO(MarcaProduto marcaProduto);

    /**
     * Converte a entidade em DTO de atualização.
     *
     * @param marcaProduto entidade de origem
     * @return DTO de atualização preenchido
     */
    MarcaProdutoUpdateDTO toUpdateDTO(MarcaProduto marcaProduto);

    /**
     * Aplica o novo nome sobre a entidade existente.
     *
     * @param dto          DTO de origem
     * @param marcaProduto entidade de destino, já carregada
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(MarcaProdutoUpdateDTO dto, @MappingTarget MarcaProduto marcaProduto);

    /**
     * Cria uma entidade nova a partir do DTO.
     *
     * @param dto DTO de origem
     * @return entidade nova, ainda sem id
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    MarcaProduto toEntity(MarcaProdutoCreateDTO dto);
}
