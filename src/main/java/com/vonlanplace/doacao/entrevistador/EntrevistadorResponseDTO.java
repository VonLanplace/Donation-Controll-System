package com.vonlanplace.doacao.entrevistador;

import java.util.UUID;

/**
 * Representação de saída de um {@link Entrevistador}.
 *
 * <p><strong>Não tem {@code senhaHash}, e essa é a razão de o record existir.</strong> Se
 * a API devolvesse a entidade, o hash de senha iria junto em toda doação. A proteção está
 * aqui, no tipo de saída, e não em {@code @JsonIgnore} sobre a entidade — o DTO é a
 * superfície pública.</p>
 *
 * <p>Não há DTO de criação ou atualização: o entrevistador não é gerenciado por
 * controller, e nada além de {@code DoacaoService} o consulta.</p>
 *
 * @param id    identificador do entrevistador
 * @param nome  nome do entrevistador
 * @param email e-mail de acesso
 */
public record EntrevistadorResponseDTO(
        UUID id,
        String nome,
        String email
) {
}
