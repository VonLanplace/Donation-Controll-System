package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProdutoMapper;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoMapper;
import org.mapstruct.*;

/**
 * Conversor entre a entidade {@link Produto} e seus DTOs.
 *
 * <p>Os sub-mapeadores de marca e tipo são injetados via {@code uses} para que o
 * aninhamento dos DTOs de resposta seja gerado automaticamente.</p>
 *
 * <p><strong>Contrato com o serviço:</strong> todos os métodos que escrevem na entidade
 * ignoram {@code id}, {@code cesta}, {@code marcaProduto} e {@code tipoProduto}. As
 * relações são preenchidas à mão pelo {@link ProdutoService}, antes da chamada ao
 * mapeador, para que a resolução por nome aconteça uma única vez e os valores já
 * resolvidos não sejam sobrescritos por {@code null}.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {TipoProdutoMapper.class, MarcaProdutoMapper.class}
)
public interface ProdutoMapper {

    /**
     * Converte a entidade em DTO de resposta, aninhando marca e tipo.
     *
     * @param produto entidade de origem
     * @return DTO de resposta correspondente
     */
    ProdutoResponseDTO toResponseDTO(Produto produto);

    /**
     * Converte a entidade em DTO de atualização, achatando marca e tipo para seus nomes.
     *
     * @param produto entidade de origem
     * @return DTO de atualização preenchido
     */
    @Mapping(target = "marcaProduto", source = "marcaProduto.nome")
    @Mapping(target = "tipoProduto", source = "tipoProduto.nome")
    ProdutoUpdateDTO toUpdateDTO(Produto produto);

    /**
     * Aplica os campos do DTO sobre a entidade existente. Propriedades nulas no DTO são
     * ignoradas.
     *
     * @param dto     DTO de origem
     * @param produto entidade de destino, já carregada e com as relações resolvidas
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cesta", ignore = true)
    @Mapping(target = "marcaProduto", ignore = true)
    @Mapping(target = "tipoProduto", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ProdutoUpdateDTO dto, @MappingTarget Produto produto);

    /**
     * Cria uma entidade nova a partir do DTO. Só o código de barras é copiado; marca,
     * tipo e cesta ficam a cargo do serviço.
     *
     * @param dto DTO de origem
     * @return entidade nova, ainda sem id e sem relações resolvidas
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cesta", ignore = true)
    @Mapping(target = "marcaProduto", ignore = true)
    @Mapping(target = "tipoProduto", ignore = true)
    Produto toEntity(ProdutoCreateDTO dto);
}
