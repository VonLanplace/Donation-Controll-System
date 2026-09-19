package com.vonlanplace.doacao.produto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {
    // Converte Entity para DTO (para preencher formulário de edição)
    //AtualizacaoCliente toAtualizacaoDto(Cliente cliente);

    // Converte DTO para Entity (para criação NOVA - ignora ID)
    //@Mapping(target = "id", ignore = true)
    //Cliente toEntityFromAtualizacao(AtualizacaoCliente dto);

    // Atualiza Entity existente com dados do DTO
    //@Mapping(target = "id", ignore = true) // Não atualiza ID
    //void updateEntityFromDto(AtualizacaoCliente dto, @MappingTarget Cliente cliente);
}
