package com.vonlanplace.doacao.recebedor.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import com.vonlanplace.doacao.support.annotations.IntegracaoTest;
import com.vonlanplace.doacao.support.config.RelogioFixoTestConfig;
import com.vonlanplace.doacao.support.fixtures.RecebedorFixtures;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Regras de negócio do recebimento de cesta contra o H2.
 */
@IntegracaoTest
@Transactional
@Import(RelogioFixoTestConfig.class)
@DisplayName("RecebimentoCestaService")
class RecebimentoCestaServiceTest {

    private static final YearMonth MES_ATUAL = YearMonth.of(2026, 1);
    private static final LocalDate HOJE = LocalDate.of(2026, 1, 15);

    @Autowired
    private RecebimentoCestaService service;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    private Recebedor recebedor;

    @BeforeEach
    void setUp() {
        Residencia residencia = residenciaRepository.save(RecebedorFixtures.residencia());
        recebedor = recebedorRepository.save(RecebedorFixtures.recebedor(residencia));
    }

    @Test
    @DisplayName("registra cesta recebida no mês atual com snapshot e data de hoje")
    void registraMesAtual() {
        RecebimentoCestaResponseDTO criado = service.registrarOuAtualizar(
                recebedor.getId(), RecebedorFixtures.createCesta(MES_ATUAL, StatusRecebimento.RECEBEU));

        assertThat(criado.id()).isNotNull();
        assertThat(criado.status()).isEqualTo(StatusRecebimento.RECEBEU);
        assertThat(criado.dataReferencia()).isEqualTo(MES_ATUAL.atDay(1));
        assertThat(criado.dataEntrega()).isEqualTo(HOJE);
        assertThat(criado.snapshot().getNomeCompleto()).isEqualTo(recebedor.getNomeCompleto());
    }

    @Test
    @DisplayName("registro do mesmo mês é idempotente e atualiza a observação")
    void registroIdempotente() {
        RecebimentoCestaResponseDTO primeiro = service.registrarOuAtualizar(
                recebedor.getId(), new RecebimentoCestaCreateDTO(
                        2026, MesRecebimento.JANEIRO, StatusRecebimento.RECEBEU, null, "primeira"));
        RecebimentoCestaResponseDTO segundo = service.registrarOuAtualizar(
                recebedor.getId(), new RecebimentoCestaCreateDTO(
                        2026, MesRecebimento.JANEIRO, StatusRecebimento.RECEBEU, null, "editada"));

        assertThat(segundo.id()).isEqualTo(primeiro.id());
        assertThat(segundo.observacao()).isEqualTo("editada");
        assertThat(service.historico(recebedor.getId())).hasSize(1);
    }

    @Test
    @DisplayName("data de entrega explícita é preservada")
    void dataEntregaExplicita() {
        RecebimentoCestaResponseDTO criado = service.registrarOuAtualizar(
                recebedor.getId(), new RecebimentoCestaCreateDTO(
                        2025, MesRecebimento.DEZEMBRO, StatusRecebimento.RECEBEU,
                        LocalDate.of(2025, 12, 20), null));

        assertThat(criado.dataEntrega()).isEqualTo(LocalDate.of(2025, 12, 20));
    }

    @ParameterizedTest(name = "status {0}")
    @EnumSource(StatusRecebimento.class)
    @DisplayName("aceita os dois status no mês atual")
    void aceitaStatusValidos(StatusRecebimento status) {
        RecebimentoCestaResponseDTO criado = service.registrarOuAtualizar(
                recebedor.getId(), RecebedorFixtures.createCesta(MES_ATUAL, status));

        assertThat(criado.status()).isEqualTo(status);
    }

    @Test
    @DisplayName("recusa mês futuro")
    void recusaMesFuturo() {
        assertThatThrownBy(() -> service.registrarOuAtualizar(
                recebedor.getId(), RecebedorFixtures.createCesta(MES_ATUAL.plusMonths(1), StatusRecebimento.RECEBEU)))
                .isInstanceOf(CestaNaoPermitidaException.class)
                .hasMessageContaining("futuro");
    }

    @Test
    @DisplayName("recusa DTO incompleto")
    void recusaDtoIncompleto() {
        assertThatThrownBy(() -> service.registrarOuAtualizar(
                recebedor.getId(), new RecebimentoCestaCreateDTO(null, null, null, null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("obrigatórios");
    }

    @Test
    @DisplayName("recusa recebedor inexistente")
    void recusaRecebedorInexistente() {
        assertThatThrownBy(() -> service.registrarOuAtualizar(
                UUID.randomUUID(), RecebedorFixtures.createCesta(MES_ATUAL, StatusRecebimento.RECEBEU)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recebedor not found");
    }

    @Test
    @DisplayName("bloqueia o quinto mês consecutivo")
    void bloqueiaQuintoMes() {
        receberEm(MES_ATUAL.minusMonths(4));
        receberEm(MES_ATUAL.minusMonths(3));
        receberEm(MES_ATUAL.minusMonths(2));
        receberEm(MES_ATUAL.minusMonths(1));

        assertThatThrownBy(() -> service.registrarOuAtualizar(
                recebedor.getId(), RecebedorFixtures.createCesta(MES_ATUAL, StatusRecebimento.RECEBEU)))
                .isInstanceOf(CestaNaoPermitidaException.class)
                .hasMessageContaining("Limite de 4");
    }

    @Test
    @DisplayName("quatro meses consecutivos ainda são permitidos")
    void permiteQuartoMes() {
        receberEm(MES_ATUAL.minusMonths(3));
        receberEm(MES_ATUAL.minusMonths(2));
        receberEm(MES_ATUAL.minusMonths(1));

        assertThat(service.registrarOuAtualizar(
                recebedor.getId(), RecebedorFixtures.createCesta(MES_ATUAL, StatusRecebimento.RECEBEU)).status())
                .isEqualTo(StatusRecebimento.RECEBEU);
    }

    @Test
    @DisplayName("recusa quebra a sequência e libera o mês seguinte")
    void recusaQuebraSequencia() {
        receberEm(MES_ATUAL.minusMonths(5));
        receberEm(MES_ATUAL.minusMonths(4));
        receberEm(MES_ATUAL.minusMonths(3));
        receberEm(MES_ATUAL.minusMonths(2));
        service.registrarOuAtualizar(
                recebedor.getId(), RecebedorFixtures.createCesta(MES_ATUAL.minusMonths(1), StatusRecebimento.RECUSOU));

        assertThat(service.registrarOuAtualizar(
                recebedor.getId(), RecebedorFixtures.createCesta(MES_ATUAL, StatusRecebimento.RECEBEU))
                .status()).isEqualTo(StatusRecebimento.RECEBEU);
    }

    @Nested
    @DisplayName("elegibilidade")
    class Elegibilidade {

        @Test
        @DisplayName("sem histórico está elegível")
        void semHistorico() {
            ElegibilidadeCestaDTO resultado = service.verificarElegibilidade(recebedor.getId(), MES_ATUAL);

            assertThat(resultado.elegivel()).isTrue();
            assertThat(resultado.mesesConsecutivos()).isZero();
            assertThat(resultado.ultimoRecebimento()).isNull();
            assertThat(resultado.proximaDataPermitida()).isEqualTo(MES_ATUAL.atDay(1));
        }

        @Test
        @DisplayName("mês já recebido não é elegível de novo")
        void jaRecebeu() {
            receberEm(MES_ATUAL);

            ElegibilidadeCestaDTO resultado = service.verificarElegibilidade(recebedor.getId(), MES_ATUAL);

            assertThat(resultado.elegivel()).isFalse();
            assertThat(resultado.motivo()).contains("já recebeu");
            assertThat(resultado.ultimoRecebimento()).isEqualTo(MES_ATUAL.atDay(1));
        }

        @Test
        @DisplayName("limite atingido informa o próximo mês permitido")
        void limiteAtingido() {
            receberEm(MES_ATUAL.minusMonths(4));
            receberEm(MES_ATUAL.minusMonths(3));
            receberEm(MES_ATUAL.minusMonths(2));
            receberEm(MES_ATUAL.minusMonths(1));

            ElegibilidadeCestaDTO resultado = service.verificarElegibilidade(recebedor.getId(), MES_ATUAL);

            assertThat(resultado.elegivel()).isFalse();
            assertThat(resultado.mesesConsecutivos()).isEqualTo(4);
            assertThat(resultado.motivo()).contains("Limite de 4");
            assertThat(resultado.proximaDataPermitida()).isEqualTo(MES_ATUAL.plusMonths(1).atDay(1));
        }

        @Test
        @DisplayName("recebedor inexistente falha")
        void recebedorInexistente() {
            assertThatThrownBy(() -> service.verificarElegibilidade(UUID.randomUUID(), MES_ATUAL))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("consultas")
    class Consultas {

        @Test
        @DisplayName("status atual é nulo sem registro")
        void statusAtualNulo() {
            assertThat(service.statusAtual(recebedor.getId(), MES_ATUAL)).isNull();
        }

        @Test
        @DisplayName("registrar status atual ignora nulo")
        void statusAtualNuloNaoGrava() {
            service.registrarStatusAtual(recebedor.getId(), null);

            assertThat(service.historico(recebedor.getId())).isEmpty();
        }

        @Test
        @DisplayName("registrar status atual grava o mês do relógio")
        void statusAtualGrava() {
            service.registrarStatusAtual(recebedor.getId(), StatusRecebimento.RECEBEU);

            assertThat(service.statusAtual(recebedor.getId(), MES_ATUAL))
                    .isEqualTo(StatusRecebimento.RECEBEU);
        }

        @Test
        @DisplayName("status do mês em lote")
        void statusDoMes() {
            receberEm(MES_ATUAL);

            Map<UUID, StatusRecebimento> mapa =
                    service.statusDoMes(List.of(recebedor.getId()), MES_ATUAL);

            assertThat(mapa).containsEntry(recebedor.getId(), StatusRecebimento.RECEBEU);
        }

        @Test
        @DisplayName("status do mês vazio devolve mapa vazio")
        void statusDoMesVazio() {
            assertThat(service.statusDoMes(List.of(), MES_ATUAL)).isEmpty();
            assertThat(service.statusDoMes(null, MES_ATUAL)).isEmpty();
        }

        @Test
        @DisplayName("histórico vem do mais recente para o mais antigo")
        void historicoOrdenado() {
            receberEm(MES_ATUAL.minusMonths(1));
            receberEm(MES_ATUAL);

            assertThat(service.historico(recebedor.getId()))
                    .extracting(RecebimentoCestaResponseDTO::dataReferencia)
                    .containsExactly(MES_ATUAL.atDay(1), MES_ATUAL.minusMonths(1).atDay(1));
        }

        @Test
        @DisplayName("histórico de recebedor inexistente falha")
        void historicoInexistente() {
            assertThatThrownBy(() -> service.historico(UUID.randomUUID()))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("recibo em PDF")
    class Recibo {

        @Test
        @DisplayName("gera PDF de cesta recebida")
        void geraPdf() {
            RecebimentoCestaResponseDTO criado = service.registrarOuAtualizar(
                    recebedor.getId(), RecebedorFixtures.createCesta(MES_ATUAL, StatusRecebimento.RECEBEU));

            assertThat(service.gerarReciboPdf(criado.id())).isNotEmpty();
        }

        @Test
        @DisplayName("recusa PDF de cesta recusada")
        void recusaPdfDeRecusada() {
            RecebimentoCestaResponseDTO criado = service.registrarOuAtualizar(
                    recebedor.getId(), RecebedorFixtures.createCesta(MES_ATUAL, StatusRecebimento.RECUSOU));

            assertThatThrownBy(() -> service.gerarReciboPdf(criado.id()))
                    .isInstanceOf(CestaNaoPermitidaException.class);
        }

        @Test
        @DisplayName("recibo inexistente falha")
        void reciboInexistente() {
            assertThatThrownBy(() -> service.gerarReciboPdf(UUID.randomUUID()))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    private void receberEm(YearMonth mes) {
        service.registrarOuAtualizar(
                recebedor.getId(), RecebedorFixtures.createCesta(mes, StatusRecebimento.RECEBEU));
    }
}
