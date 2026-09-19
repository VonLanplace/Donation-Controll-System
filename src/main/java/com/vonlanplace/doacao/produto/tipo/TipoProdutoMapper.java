package com.vonlanplace.doacao.produto.tipo;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TipoProdutoMapper {
    TipoProdutoResponseDTO toResponseDTO(TipoProduto entity);

    TipoProdutoUpdateDTO toUpdateDTO(TipoProduto tipoProduto);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(TipoProdutoUpdateDTO dto, @MappingTarget TipoProduto tipoProduto);

    @Mapping(target = "id", ignore = true)
    TipoProduto toEntity(TipoProdutoCreateDTO dto);
}
