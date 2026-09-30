package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.produto.ProdutoMapper;
import org.mapstruct.Mapper;

/**
 * Conversor de leitura de {@link DoacaoProduto} para
 * {@link DoacaoProdutoResponseDTO}.
 *
 * <p>Só no sentido entidade → DTO, pelo mesmo motivo de {@link DoacaoMapper}. O DTO
 * achata a relação com {@code Produto} em um único {@code produtoId}, então o
 * {@link ProdutoMapper} listado em {@code uses} é usado para montar o produto aninhado do
 * item, e não para a escrita.</p>
 */
@Mapper(componentModel = "spring", uses = {ProdutoMapper.class})
public interface DoacaoProdutoMapper {

    /**
     * Converte o item em DTO de resposta.
     *
     * @param doacaoProduto item de origem
     * @return DTO de resposta preenchido, com o produto aninhado
     */
    DoacaoProdutoResponseDTO toResponseDTO(DoacaoProduto doacaoProduto);
}
