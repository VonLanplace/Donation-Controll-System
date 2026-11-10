package com.vonlanplace.doacao.support.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

/**
 * Fixa o relógio da aplicação em 15/01/2026, horário de São Paulo.
 *
 * <p>Importe com {@code @Import(RelogioFixoTestConfig.class)} nos testes que
 * dependem de mês ou de data. Assim a virada do mês real não muda o resultado.</p>
 */
@TestConfiguration
public class RelogioFixoTestConfig {

    public static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    public static final Instant INSTANTE = Instant.parse("2026-01-15T15:00:00Z");
    public static final LocalDate HOJE = LocalDate.ofInstant(INSTANTE, ZONA);
    public static final YearMonth MES_ATUAL = YearMonth.from(HOJE);

    @Bean
    @Primary
    Clock clockFixo() {
        return Clock.fixed(INSTANTE, ZONA);
    }
}
