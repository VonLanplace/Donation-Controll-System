package com.vonlanplace.doacao.recebedor.form;

import com.vonlanplace.doacao.recebedor.OrgaoEmissorRG;
import com.vonlanplace.doacao.recebedor.RecebedorCreateDTO;
import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;
import com.vonlanplace.doacao.recebedor.RecebedorUpdateDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioResponseDTO;
import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import com.vonlanplace.doacao.recebedor.dependente.DependenteResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaCreateDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaUpdateDTO;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaResponseDTO;
import com.vonlanplace.doacao.support.fixtures.RecebedorFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Conversão entre formulário, entidades e DTOs.
 */
@Tag("unit")
@DisplayName("RecebedorFormMapper")
class RecebedorFormMapperTest {

    private final RecebedorFormMapper mapper = new RecebedorFormMapper();

    @Test
    @DisplayName("monta o formulário completo com listas mutáveis")
    void montaFormulario() {
        UUID residenciaId = UUID.randomUUID();
        RecebedorResponseDTO recebedor = recebedor(residenciaId);
        ResidenciaResponseDTO residencia = recebedor.residencia();

        RecebedorFormDTO form = mapper.toFormDTO(
                recebedor,
                List.of(new DependenteResponseDTO(
                        UUID.randomUUID(), "Filho", 5, LocalDate.of(2020, 1, 1), recebedor)),
                List.of(new AuxilioResponseDTO(
                        UUID.randomUUID(), new BigDecimal("200.00"), "Bolsa Família", null, recebedor)),
                List.of(new DespesaResponseDTO(
                        UUID.randomUUID(), new BigDecimal("300.00"), "Aluguel", null, residencia)),
                StatusRecebimento.RECEBEU
        );

        assertThat(form.getNomeCompleto()).isEqualTo("Ana Souza");
        assertThat(form.getCpf()).isEqualTo("11122233344");
        assertThat(form.getStatusCesta()).isEqualTo(StatusRecebimento.RECEBEU);
        assertThat(form.getResidencia().getLogradouro()).isEqualTo("Rua A");
        assertThat(form.getDependentes()).extracting(DependenteFormDTO::getNome)
                .containsExactly("Filho");
        assertThat(form.getAuxilios()).extracting(AuxilioFormDTO::getFonte)
                .containsExactly("Bolsa Família");
        assertThat(form.getDespesas()).extracting(DespesaFormDTO::getFonte)
                .containsExactly("Aluguel");

        form.getDependentes().add(RecebedorFixtures.dependenteForm("Outro", 1, null));
        assertThat(form.getDependentes()).hasSize(2);
    }

    @Test
    @DisplayName("converte residência para criação e atualização")
    void converteResidencia() {
        ResidenciaFormDTO form = RecebedorFixtures.residenciaForm();

        ResidenciaCreateDTO criacao = mapper.toResidenciaCreateDTO(form);
        ResidenciaUpdateDTO atualizacao = mapper.toResidenciaUpdateDTO(form);

        assertThat(criacao.logradouro()).isEqualTo("Rua A");
        assertThat(criacao.cidade()).isEqualTo("São Paulo");
        assertThat(criacao.tipoOcupacao()).isEqualTo(TipoOcupacao.PROPRIEDADE);
        assertThat(atualizacao.logradouro()).isEqualTo("Rua A");
        assertThat(atualizacao.cidade()).isEqualTo("São Paulo");
        assertThat(atualizacao.tipoResidencia()).isEqualTo(TipoResidencia.ALVENARIA_TRADICIONAL);
    }

    @Test
    @DisplayName("converte formulário para criação e atualização do recebedor")
    void converteRecebedor() {
        RecebedorFormDTO form = RecebedorFixtures.formularioValido();
        UUID residenciaId = UUID.randomUUID();

        RecebedorCreateDTO criacao = mapper.toCreateDTO(form, residenciaId);
        RecebedorUpdateDTO atualizacao = mapper.toUpdateDTO(form, residenciaId);

        assertThat(criacao.nomeCompleto()).isEqualTo(form.getNomeCompleto());
        assertThat(criacao.residenciaId()).isEqualTo(residenciaId);
        assertThat(atualizacao.nomeCompleto()).isEqualTo(form.getNomeCompleto());
        assertThat(atualizacao.residenciaId()).isEqualTo(residenciaId);
    }

    @Test
    @DisplayName("converte linhas filhas para criação e atualização")
    void converteFilhos() {
        UUID recebedorId = UUID.randomUUID();
        UUID residenciaId = UUID.randomUUID();
        DependenteFormDTO dependente = RecebedorFixtures.dependenteForm("Filho", 5, LocalDate.of(2020, 1, 1));
        AuxilioFormDTO auxilio = RecebedorFixtures.auxilioForm("200.00", "Bolsa", null);
        DespesaFormDTO despesa = RecebedorFixtures.despesaForm("300.00", "Aluguel", null);

        assertThat(mapper.toDependenteCreateDTO(dependente, recebedorId).recebedorId())
                .isEqualTo(recebedorId);
        assertThat(mapper.toDependenteUpdateDTO(dependente, recebedorId).recebedorId())
                .isEqualTo(recebedorId);
        assertThat(mapper.toAuxilioCreateDTO(auxilio, recebedorId).recebedorId())
                .isEqualTo(recebedorId);
        assertThat(mapper.toAuxilioUpdateDTO(auxilio, recebedorId).recebedorId())
                .isEqualTo(recebedorId);
        assertThat(mapper.toDespesaCreateDTO(despesa, residenciaId).residenciaId())
                .isEqualTo(residenciaId);
        assertThat(mapper.toDespesaUpdateDTO(despesa, residenciaId).residenciaId())
                .isEqualTo(residenciaId);
    }

    private RecebedorResponseDTO recebedor(UUID residenciaId) {
        ResidenciaResponseDTO residencia = new ResidenciaResponseDTO(
                residenciaId, "Rua A", "10", "01001000", null,
                "São Paulo", "Centro", 2, 1,
                TipoOcupacao.PROPRIEDADE, TipoResidencia.ALVENARIA_TRADICIONAL);
        return new RecebedorResponseDTO(
                UUID.randomUUID(), "Ana Souza", "11122233344", "123456",
                OrgaoEmissorRG.SSP, LocalDate.of(1990, 1, 1), "11999999999",
                null, true, "ana@email.com", "ana@pix.com",
                false, true, true, "Primeira visita.", residencia);
    }
}
