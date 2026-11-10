package com.vonlanplace.doacao.recebedor.cesta;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Conversão do número do mês para o enum.
 */
@Tag("unit")
@DisplayName("MesRecebimento")
class MesRecebimentoTest {

    @ParameterizedTest(name = "mês {0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12})
    void resolveMesesValidos(int numero) {
        assertThat(MesRecebimento.de(numero).getNumero()).isEqualTo(numero);
    }

    @ParameterizedTest(name = "mês inválido {0}")
    @ValueSource(ints = {-1, 0, 13, 99})
    void recusaMesesInvalidos(int numero) {
        assertThatThrownBy(() -> MesRecebimento.de(numero))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mês inválido");
    }
}
