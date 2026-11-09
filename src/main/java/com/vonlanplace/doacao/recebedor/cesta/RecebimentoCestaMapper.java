package com.vonlanplace.doacao.recebedor.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Conversor entre {@link RecebimentoCesta}, {@link Recebedor} e seus DTOs.
 *
 * <p><strong>O snapshot é achatado à mão.</strong> O endereço vive em
 * {@code Recebedor.residencia}, mas no snapshot as colunas são planas. Os
 * {@code @Mapping} abaixo fazem essa ponte; os demais campos têm o mesmo nome e são
 * copiados automaticamente.</p>
 */
@Mapper(componentModel = "spring")
public interface RecebimentoCestaMapper {

    /**
     * Converte o registro em DTO de resposta.
     *
     * @param recebimento entidade de origem
     * @return DTO preenchido
     */
    @Mapping(target = "recebedorId", source = "recebedor.id")
    RecebimentoCestaResponseDTO toResponseDTO(RecebimentoCesta recebimento);

    /**
     * Copia os dados atuais do recebedor para o snapshot do recibo.
     *
     * @param recebedor entidade de origem
     * @return snapshot preenchido
     */
    @Mapping(target = "logradouro", source = "residencia.logradouro")
    @Mapping(target = "numero", source = "residencia.numero")
    @Mapping(target = "complemento", source = "residencia.complemento")
    @Mapping(target = "bairro", source = "residencia.bairro")
    @Mapping(target = "cidade", source = "residencia.cidade")
    @Mapping(target = "cep", source = "residencia.cep")
    SnapshotRecebedor toSnapshot(Recebedor recebedor);
}
