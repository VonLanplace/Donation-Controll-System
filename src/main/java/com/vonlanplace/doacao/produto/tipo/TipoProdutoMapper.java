package com.vonlanplace.doacao.produto.tipo;

import org.mapstruct.*;

/**
 * Conversor entre {@link TipoProduto} e seus DTOs.
 *
 * <p>Conversão direta de um único campo {@code nome}. Os métodos que escrevem na
 * entidade ignoram {@code id} e {@code produtos}: o id é gerado pelo banco e a coleção
 * é mantida pelo lado dono da relação, {@code Produto.tipoProduto}.</p>
 */
@Mapper(componentModel = "spring")
public interface TipoProdutoMapper {

    /**
     * Converte a entidade em DTO de resposta.
     *
     * @param tipoProduto entidade de origem
     * @return DTO contendo apenas o nome
     */
    TipoProdutoResponseDTO toResponseDTO(TipoProduto tipoProduto);

    /**
     * Converte a entidade em DTO de atualização.
     *
     * @param tipoProduto entidade de origem
     * @return DTO de atualização preenchido
     */
    TipoProdutoUpdateDTO toUpdateDTO(TipoProduto tipoProduto);

    /**
     * Aplica o novo nome sobre a entidade existente.
     *
     * @param dto         DTO de origem
     * @param tipoProduto entidade de destino, já carregada
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TipoProdutoUpdateDTO dto, @MappingTarget TipoProduto tipoProduto);

    /**
     * Cria uma entidade nova a partir do DTO.
     *
     * @param dto DTO de origem
     * @return entidade nova, ainda sem id
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    TipoProduto toEntity(TipoProdutoCreateDTO dto);
}
