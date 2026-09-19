package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProdutoCreateDTO;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoMapper;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {TipoProdutoMapper.class, MarcaProdutoMapper.class}
)
public interface ProdutoMapper {
    ProdutoResponseDTO toResponseDTO(Produto produto);

    // Converte Entity para DTO (para preencher formulário de edição)
    ProdutoUpdateDTO toUpdateDTO(Produto produto);

    // Atualiza Entity existente com dados do DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cesta", ignore = true)
    @Mapping(target = "marcaProduto.id", source = "marcaProdutoId")
    @Mapping(target = "tipoProduto.id", source = "tipoProdutoId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ProdutoUpdateDTO dto, @MappingTarget Produto produto);

    // Converte DTO para Entity (para criação NOVA - ignora ID)
    @Mapping(target = "id", ignore = true)
    Produto toEntity(ProdutoCreateDTO dto);
}
