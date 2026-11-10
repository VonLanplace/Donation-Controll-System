package com.vonlanplace.doacao.recebedor.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.support.fixtures.RecebedorFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Geração do recibo em PDF a partir do snapshot.
 */
@Tag("unit")
@DisplayName("RecebedorCestaPdfService")
class RecebedorCestaPdfServiceTest {

    private static final Clock RELOGIO = Clock.fixed(
            Instant.parse("2026-01-15T15:00:00Z"),
            ZoneId.of("America/Sao_Paulo"));

    private final RecebedorCestaPdfService service = new RecebedorCestaPdfService(RELOGIO);

    @Test
    @DisplayName("gera PDF válido")
    void geraPdf() {
        RecebimentoCesta recebimento = recebimento();

        byte[] pdf = service.gerar(recebimento);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("campos opcionais nulos não quebram o PDF")
    void camposNulos() {
        RecebimentoCesta recebimento = recebimento();
        recebimento.getSnapshot().setEmail(null);
        recebimento.getSnapshot().setTelefonePessoal(null);
        recebimento.getSnapshot().setComplemento(null);

        assertThat(service.gerar(recebimento)).isNotEmpty();
    }

    @Test
    @DisplayName("recibo sem snapshot é recusado")
    void semSnapshot() {
        RecebimentoCesta recebimento = recebimento();
        recebimento.setSnapshot(null);

        assertThatThrownBy(() -> service.gerar(recebimento))
                .isInstanceOf(CestaNaoPermitidaException.class)
                .hasMessageContaining("snapshot");
    }

    private RecebimentoCesta recebimento() {
        Recebedor recebedor = RecebedorFixtures.recebedor(RecebedorFixtures.residencia());
        RecebimentoCesta recebimento = RecebedorFixtures.recebimento(
                recebedor, YearMonth.of(2026, 1), StatusRecebimento.RECEBEU);
        recebimento.setId(UUID.randomUUID());
        recebimento.setDataEntrega(LocalDate.of(2026, 1, 10));
        recebimento.setObservacao("Entrega normal");
        return recebimento;
    }
}
