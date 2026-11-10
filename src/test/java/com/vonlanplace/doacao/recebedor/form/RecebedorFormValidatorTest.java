package com.vonlanplace.doacao.recebedor.form;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import com.vonlanplace.doacao.support.fixtures.RecebedorFixtures;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Validação das linhas dinâmicas do formulário de recebedor.
 */
@Tag("unit")
@DisplayName("RecebedorFormValidator")
class RecebedorFormValidatorTest {

    private static final ZoneId ZONA = ZoneId.of("America/Sao_Paulo");
    private static final Clock RELOGIO = Clock.fixed(
            Instant.parse("2026-01-15T15:00:00Z"), ZONA);
    private static final LocalDate HOJE = LocalDate.of(2026, 1, 15);

    private final RecebedorFormValidator validator = new RecebedorFormValidator(RELOGIO);

    @Nested
    @DisplayName("dependentes")
    class Dependentes {

        @ParameterizedTest(name = "nome [{0}] com idade preenchida é recusado")
        @NullSource
        @EmptySource
        @ValueSource(strings = {" ", "\t"})
        void nomeObrigatorio(String nome) {
            RecebedorFormDTO form = formulario();
            form.getDependentes().add(RecebedorFixtures.dependenteForm(nome, 5, null));

            Errors erros = validar(form);

            assertThat(erros.getFieldError("dependentes[0].nome")).isNotNull();
        }

        @ParameterizedTest(name = "nome com {0} caracteres")
        @ValueSource(ints = {150, 151})
        void limiteDoNome(int tamanho) {
            RecebedorFormDTO form = formulario();
            form.getDependentes().add(
                    RecebedorFixtures.dependenteForm("A".repeat(tamanho), 5, null));

            Errors erros = validar(form);

            if (tamanho > 150) {
                assertThat(erros.getFieldError("dependentes[0].nome")).isNotNull();
            } else {
                assertThat(erros.getFieldError("dependentes[0].nome")).isNull();
            }
        }

        @ParameterizedTest(name = "idade {0}")
        @ValueSource(ints = {-10, -1})
        void idadeNaoPodeSerNegativa(int idade) {
            RecebedorFormDTO form = formulario();
            form.getDependentes().add(RecebedorFixtures.dependenteForm("Filho", idade, null));

            assertThat(validar(form).getFieldError("dependentes[0].idade")).isNotNull();
        }

        @ParameterizedTest(name = "nascimento em {0}")
        @MethodSource("datasDeNascimento")
        void nascimentoPrecisaSerPassado(LocalDate data, boolean invalida) {
            RecebedorFormDTO form = formulario();
            form.getDependentes().add(RecebedorFixtures.dependenteForm("Filho", 5, data));

            Errors erros = validar(form);

            assertThat(erros.getFieldError("dependentes[0].dataNascimento") != null)
                    .isEqualTo(invalida);
        }

        static Object[][] datasDeNascimento() {
            return new Object[][]{
                    {HOJE.minusDays(1), false},
                    {HOJE, true},
                    {HOJE.plusDays(1), true}
            };
        }
    }

    @Nested
    @DisplayName("auxílios e despesas")
    class Valores {

        @ParameterizedTest(name = "valor [{0}] com fonte preenchida")
        @NullSource
        @ValueSource(strings = {"0", "0.00", "-0.01"})
        void valorPrecisaSerPositivo(String valor) {
            RecebedorFormDTO form = formulario();
            form.getAuxilios().add(RecebedorFixtures.auxilioForm(valor, "Bolsa Família", null));

            Errors erros = validar(form);

            assertThat(erros.getFieldError("auxilios[0].valor")).isNotNull();
        }

        @Test
        @DisplayName("valor 0.01 é aceito")
        void valorMinimoAceito() {
            RecebedorFormDTO form = formulario();
            form.getAuxilios().add(RecebedorFixtures.auxilioForm("0.01", "Bolsa Família", null));

            assertThat(validar(form).getFieldError("auxilios[0].valor")).isNull();
        }

        @ParameterizedTest(name = "fonte [{0}] com valor preenchido")
        @NullSource
        @EmptySource
        @ValueSource(strings = {" ", "\t"})
        void fonteObrigatoria(String fonte) {
            RecebedorFormDTO form = formulario();
            form.getDespesas().add(RecebedorFixtures.despesaForm("10.00", fonte, null));

            assertThat(validar(form).getFieldError("despesas[0].fonte")).isNotNull();
        }

        @ParameterizedTest(name = "fonte com {0} caracteres")
        @ValueSource(ints = {100, 101})
        void limiteDaFonte(int tamanho) {
            RecebedorFormDTO form = formulario();
            form.getDespesas().add(
                    RecebedorFixtures.despesaForm("10.00", "F".repeat(tamanho), null));

            Errors erros = validar(form);

            assertThat(erros.getFieldError("despesas[0].fonte") != null)
                    .isEqualTo(tamanho > 100);
        }

        @ParameterizedTest(name = "descrição com {0} caracteres")
        @ValueSource(ints = {500, 501})
        void limiteDaDescricao(int tamanho) {
            RecebedorFormDTO form = formulario();
            form.getAuxilios().add(
                    RecebedorFixtures.auxilioForm("10.00", "ONG", "D".repeat(tamanho)));

            Errors erros = validar(form);

            assertThat(erros.getFieldError("auxilios[0].descricao") != null)
                    .isEqualTo(tamanho > 500);
        }

        @Test
        @DisplayName("linha de despesa só com valor exige fonte")
        void despesaParcial() {
            RecebedorFormDTO form = formulario();
            DespesaFormDTO despesa = new DespesaFormDTO();
            despesa.setValor(new BigDecimal("10.00"));
            form.getDespesas().add(despesa);

            assertThat(validar(form).getFieldError("despesas[0].fonte")).isNotNull();
        }
    }

    @Nested
    @DisplayName("linhas vazias")
    class LinhasVazias {

        @Test
        @DisplayName("linhas completamente vazias são ignoradas")
        void linhasVaziasIgnoradas() {
            RecebedorFormDTO form = formulario();
            form.getDependentes().add(new DependenteFormDTO());
            form.getAuxilios().add(new AuxilioFormDTO());
            form.getDespesas().add(new DespesaFormDTO());

            assertThat(validar(form).getAllErrors()).isEmpty();
        }

        @Test
        @DisplayName("linha nula é ignorada")
        void linhaNulaIgnorada() {
            RecebedorFormDTO form = formulario();
            form.getDependentes().add(null);
            form.getAuxilios().add(null);
            form.getDespesas().add(null);

            assertThat(validar(form).getAllErrors()).isEmpty();
        }

        @Test
        @DisplayName("listas nulas não quebram a validação")
        void listasNulas() {
            RecebedorFormDTO form = formulario();
            form.setDependentes(null);
            form.setAuxilios(null);
            form.setDespesas(null);

            assertThat(validar(form).getAllErrors()).isEmpty();
        }
    }

    @Test
    @DisplayName("formulário válido não produz erros")
    void formularioValido() {
        assertThat(validar(formulario()).getAllErrors()).isEmpty();
    }

    @Test
    @DisplayName("supports aceita apenas RecebedorFormDTO")
    void supports() {
        assertThat(validator.supports(RecebedorFormDTO.class)).isTrue();
        assertThat(validator.supports(Object.class)).isFalse();
    }

    private Errors validar(RecebedorFormDTO form) {
        Errors erros = new BeanPropertyBindingResult(form, "recebedorForm");
        validator.validate(form, erros);
        return erros;
    }

    private RecebedorFormDTO formulario() {
        RecebedorFormDTO form = new RecebedorFormDTO();
        form.getDependentes().clear();
        form.getAuxilios().clear();
        form.getDespesas().clear();
        return form;
    }
}
