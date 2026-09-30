package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.entrevistador.EntrevistadorMapper;
import com.vonlanplace.doacao.recebedor.RecebedorMapper;
import org.mapstruct.Mapper;

/**
 * Conversor de leitura de {@link Doacao} para {@link DoacaoResponseDTO}.
 *
 * <p>Só existe o sentido entidade → DTO. A escrita de {@code Doacao} é feita à mão em
 * {@code DoacaoService}: os produtos chegam como lista de DTOs com
 * {@code produtoId} e quantidade, e cada item precisa ser ligado tanto ao
 * {@code Produto} resolvido quanto à própria {@code Doacao}. MapStruct não monta isso
 * sem código intermediário, e o vínculo dos dois lados da chave composta é exatamente o
 * que o {@code @IdClass} exige.</p>
 *
 * <p>Os mappers aninhados importam a proteção de senha:
 * {@code EntrevistadorMapper} converte para {@code EntrevistadorResponseDTO}, que não
 * expõe {@code senhaHash}.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {RecebedorMapper.class, EntrevistadorMapper.class, DoacaoProdutoMapper.class}
)
public interface DoacaoMapper {

    /**
     * Converte a entidade em DTO de resposta, aninhando recebedor, entrevistador e a
     * lista completa de itens.
     *
     * @param doacao entidade de origem
     * @return DTO de resposta preenchido
     */
    DoacaoResponseDTO toResponseDTO(Doacao doacao);
}
