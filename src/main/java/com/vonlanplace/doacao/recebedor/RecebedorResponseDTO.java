package com.vonlanplace.doacao.recebedor;

import com.vonlanplace.doacao.recebedor.residencia.ResidenciaResponseDTO;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Representação de saída de um {@link Recebedor}.
 *
 * <p>Exposta integralmente, inclusive o CPF e a chave PIX: o recebedor é gerenciado pelo
 * entrevistador e a API não tem sessão de usuário nem filtro por papel. Ao aninhar a
 * residência inteira, este DTO carrega a lista de todos os moradores daquele
 * endereço.</p>
 *
 * @param id               identificador do recebedor
 * @param nomeCompleto     nome completo
 * @param cpf              CPF sem pontuação
 * @param rgNum            número do RG
 * @param rgEmissor        órgão emissor do RG
 * @param dataNascimento   data de nascimento
 * @param telefonePessoal  telefone com DDD
 * @param telefoneRecado   telefone de recado
 * @param leEscreve        se a pessoa sabe ler e escrever
 * @param email            e-mail
 * @param pix              chave PIX
 * @param necessitaJuridico se precisa de acompanhamento jurídico
 * @param necessitaPsicologo se precisa de acompanhamento psicológico
 * @param comentarios      observações da entrevista
 * @param residencia       residência e seus moradores
 */
public record RecebedorResponseDTO(
        UUID id,
        String nomeCompleto,
        String cpf,
        String rgNum,
        OrgaoEmissorRG rgEmissor,
        LocalDate dataNascimento,
        String telefonePessoal,
        String telefoneRecado,
        boolean leEscreve,
        String email,
        String pix,
        boolean necessitaJuridico,
        boolean necessitaPsicologo,
        boolean parecerEntrevistador,
        String comentarios,
        ResidenciaResponseDTO residencia
) {
}
