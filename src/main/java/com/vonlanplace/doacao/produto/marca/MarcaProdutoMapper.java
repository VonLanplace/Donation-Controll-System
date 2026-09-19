package com.vonlanplace.doacao.produto.marca;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MarcaProdutoMapper {
    MarcaProdutoResponseDTO toResponseDTO(MarcaProduto marcaProduto);

    MarcaProdutoUpdateDTO toUpdateDTO(MarcaProduto marcaProduto);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(MarcaProdutoUpdateDTO dto, @MappingTarget MarcaProduto marcaProduto);

    @Mapping(target = "id", ignore = true)
    MarcaProduto toEntity(MarcaProdutoCreateDTO dto);
    
}
