package com.vonlanplace.doacao.recebedor.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import com.vonlanplace.doacao.support.annotations.IntegracaoTest;
import com.vonlanplace.doacao.support.fixtures.RecebedorFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Exportação CSV dos recebimentos.
 */
@IntegracaoTest
@Transactional
@DisplayName("RecebedorCestaCsvService")
class RecebedorCestaCsvServiceTest {

    @Autowired
    private RecebedorCestaCsvService service;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private RecebimentoCestaRepository recebimentoCestaRepository;

    private Residencia residencia;

    @BeforeEach
    void setUp() {
        residencia = residenciaRepository.save(RecebedorFixtures.residencia());
    }

    @ParameterizedTest(name = "período inválido {0} a {1}")
    @MethodSource("periodosInvalidos")
    @DisplayName("recusa período inválido")
    void recusaPeriodoInvalido(YearMonth inicio, YearMonth fim, String mensagem) {
        assertThatThrownBy(() -> service.exportar(inicio, fim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(mensagem);
    }

    static Stream<Arguments> periodosInvalidos() {
        return Stream.of(
                Arguments.of(null, YearMonth.of(2026, 1), "Informe o mês"),
                Arguments.of(YearMonth.of(2026, 1), null, "Informe o mês"),
                Arguments.of(YearMonth.of(2026, 2), YearMonth.of(2026, 1), "anterior ou igual"),
                Arguments.of(YearMonth.of(2020, 1), YearMonth.of(2025, 2), "período máximo")
        );
    }

    @Test
    @DisplayName("período vazio gera apenas o cabeçalho")
    void periodoVazio() {
        List<String> linhas = linhas(service.exportar(YearMonth.of(2026, 1), YearMonth.of(2026, 1)));

        assertThat(linhas).hasSize(1);
        assertThat(linhas.get(0)).startsWith("recebedor_id;nome_completo;cpf;total_recebimentos");
        assertThat(linhas.get(0)).contains("2026-01_status");
    }

    @Test
    @DisplayName("exporta somente recebedores com RECEBEU")
    void exportaSomenteRecebeu() {
        Recebedor ana = recebedor("Ana Souza");
        Recebedor bia = recebedor("Bia Lima");
        persistir(ana, YearMonth.of(2026, 1), StatusRecebimento.RECEBEU, "Ana Souza");
        persistir(bia, YearMonth.of(2026, 1), StatusRecebimento.RECUSOU, "Bia Lima");

        List<String> linhas = linhas(service.exportar(YearMonth.of(2026, 1), YearMonth.of(2026, 1)));

        assertThat(linhas).hasSize(2);
        assertThat(linhas.get(1)).contains("Ana Souza").doesNotContain("Bia Lima");
    }

    @Test
    @DisplayName("meses sem recebimento ficam em branco")
    void mesesSemRecebimento() {
        Recebedor ana = recebedor("Ana Souza");
        persistir(ana, YearMonth.of(2026, 1), StatusRecebimento.RECEBEU, "Ana Souza");
        persistir(ana, YearMonth.of(2026, 3), StatusRecebimento.RECEBEU, "Ana Souza");

        List<String> linhas = linhas(service.exportar(YearMonth.of(2026, 1), YearMonth.of(2026, 3)));
        String[] cabecalho = linhas.get(0).split(";", -1);
        String[] valores = linhas.get(1).split(";", -1);

        assertThat(valores[indice(cabecalho, "2026-01_status")]).isEqualTo("Recebeu");
        assertThat(valores[indice(cabecalho, "2026-02_status")]).isEmpty();
        assertThat(valores[indice(cabecalho, "2026-03_status")]).isEqualTo("Recebeu");
        assertThat(valores[indice(cabecalho, "total_recebimentos")]).isEqualTo("2");
        assertThat(valores[indice(cabecalho, "ultimo_mes_recebido")]).isEqualTo("2026-03");
    }

    @Test
    @DisplayName("inclui snapshot, entrega e observação")
    void incluiDadosDoSnapshot() {
        Recebedor ana = recebedor("Ana Souza");
        RecebimentoCesta recebimento = persistir(
                ana, YearMonth.of(2026, 1), StatusRecebimento.RECEBEU, "Ana Souza");
        recebimento.setDataEntrega(LocalDate.of(2026, 1, 10));
        recebimento.setObservacao("Entrega normal");
        recebimentoCestaRepository.save(recebimento);

        List<String> linhas = linhas(service.exportar(YearMonth.of(2026, 1), YearMonth.of(2026, 1)));
        String[] cabecalho = linhas.get(0).split(";", -1);
        String[] valores = linhas.get(1).split(";", -1);

        assertThat(valores[indice(cabecalho, "2026-01_data_entrega")]).isEqualTo("10/01/2026");
        assertThat(valores[indice(cabecalho, "2026-01_observacao")]).isEqualTo("Entrega normal");
        assertThat(valores[indice(cabecalho, "2026-01_nome_completo")]).isEqualTo("Ana Souza");
        assertThat(valores[indice(cabecalho, "2026-01_cpf")]).isNotBlank();
        assertThat(valores[indice(cabecalho, "2026-01_logradouro")]).isEqualTo("Rua das Flores");
        assertThat(valores[indice(cabecalho, "2026-01_le_escreve")]).isEqualTo("Sim");
    }

    @Test
    @DisplayName("ordena linhas pelo nome do snapshot")
    void ordenaLinhas() {
        Recebedor zelia = recebedor("Zélia");
        Recebedor ana = recebedor("Ana");
        persistir(zelia, YearMonth.of(2026, 1), StatusRecebimento.RECEBEU, "Zélia");
        persistir(ana, YearMonth.of(2026, 1), StatusRecebimento.RECEBEU, "Ana");

        List<String> linhas = linhas(service.exportar(YearMonth.of(2026, 1), YearMonth.of(2026, 1)));

        assertThat(linhas.get(1)).contains("Ana");
        assertThat(linhas.get(2)).contains("Zélia");
    }

    @Test
    @DisplayName("escapa campos com ponto e vírgula")
    void escapaCampos() {
        Recebedor ana = recebedor("Ana Souza");
        persistir(ana, YearMonth.of(2026, 1), StatusRecebimento.RECEBEU, "Silva; Ana");

        List<String> linhas = linhas(service.exportar(YearMonth.of(2026, 1), YearMonth.of(2026, 1)));

        assertThat(linhas.get(1)).contains("\"Silva; Ana\"");
    }

    private Recebedor recebedor(String nome) {
        Recebedor recebedor = RecebedorFixtures.recebedor(residencia);
        recebedor.setNomeCompleto(nome);
        return recebedorRepository.save(recebedor);
    }

    private RecebimentoCesta persistir(
            Recebedor recebedor, YearMonth mes, StatusRecebimento status, String nomeSnapshot) {
        RecebimentoCesta recebimento = RecebedorFixtures.recebimento(recebedor, mes, status);
        recebimento.getSnapshot().setNomeCompleto(nomeSnapshot);
        return recebimentoCestaRepository.save(recebimento);
    }

    private List<String> linhas(byte[] csv) {
        String texto = new String(csv, StandardCharsets.UTF_8);
        if (texto.startsWith("\uFEFF")) {
            texto = texto.substring(1);
        }
        return texto.lines().toList();
    }

    private int indice(String[] cabecalho, String coluna) {
        return java.util.Arrays.asList(cabecalho).indexOf(coluna);
    }
}
