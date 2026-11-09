package com.vonlanplace.doacao.recebedor;

import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaService;
import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaCreateDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaService;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Confere a ida e volta do status da cesta e do parecer do entrevistador.
 *
 * <p><strong>Estes são os testes que impedem o mapeamento silencioso.</strong>
 * O status saiu do recebedor e passou a viver no histórico mensal. Aqui o
 * recebedor é criado, relido da base, atualizado e relido de novo, e o status
 * do mês tem que chegar idêntico nas duas pontas.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("Campos novos do recebedor")
class RecebedorCamposNovosTest {

    @Autowired
    private RecebedorService recebedorService;

    @Autowired
    private RecebimentoCestaService recebimentoCestaService;

    @Autowired
    private ResidenciaService residenciaService;

    @Test
    @DisplayName("statusCesta e parecerEntrevistador sobrevivem ao create e ao update")
    void camposSobrevivemAoPersistir() {
        ResidenciaResponseDTO residencia = residenciaService.create(
                new ResidenciaCreateDTO(
                        "Rua das Flores", "100", "01001000", "", "São Paulo",
                        "Centro", 4, 2, TipoOcupacao.PROPRIEDADE, TipoResidencia.ALVENARIA_TRADICIONAL
                ));

        RecebedorCreateDTO createDTO = new RecebedorCreateDTO(
                "Ana Souza", "11122233344", "123456", null,
                LocalDate.of(1990, 1, 1), "11900000000", "11988888888",
                true, "ana@exemplo.org", "11122233344",
                false, true, StatusRecebimento.RECEBEU, false, "Primeira visita.", residencia.id());

        RecebedorResponseDTO criado = recebedorService.create(createDTO);

        assertThat(recebimentoCestaService.statusAtual(criado.id(), YearMonth.now()))
                .as("o create perdeu statusCesta")
                .isEqualTo(StatusRecebimento.RECEBEU);
        assertThat(criado.parecerEntrevistador()).as("o create perdeu parecerEntrevistador").isFalse();

        RecebedorResponseDTO relido = recebedorService.findById(criado.id());
        assertThat(relido.parecerEntrevistador()).isFalse();

        RecebedorUpdateDTO updateDTO = new RecebedorUpdateDTO(
                createDTO.nomeCompleto(), createDTO.cpf(), createDTO.rgNum(), null,
                createDTO.dataNascimento(), createDTO.telefonePessoal(), createDTO.telefoneRecado(),
                createDTO.leEscreve(), createDTO.email(), createDTO.pix(),
                createDTO.necessitaJuridico(), createDTO.necessitaPsicologo(),
                StatusRecebimento.RECUSOU, true, createDTO.comentarios(), residencia.id());

        RecebedorResponseDTO atualizado = recebedorService.update(criado.id(), updateDTO);

        assertThat(recebimentoCestaService.statusAtual(atualizado.id(), YearMonth.now()))
                .as("o update não trocou statusCesta")
                .isEqualTo(StatusRecebimento.RECUSOU);
        assertThat(atualizado.parecerEntrevistador()).as("o update não trocou parecerEntrevistador").isTrue();
    }

    @Test
    @DisplayName("o id fica estável entre create e read")
    void idPermaneceEstavel() {
        ResidenciaResponseDTO residencia = residenciaService.create(
                new ResidenciaCreateDTO(
                        "Rua A", "1", "02002000", "", "Campinas", "Centro",
                        2, 1, TipoOcupacao.LOCACAO, TipoResidencia.MADEIRA
                ));

        UUID antesCreate = UUID.randomUUID();

        RecebedorResponseDTO criado = recebedorService.create(basico(residencia.id()));

        assertThat(criado.id()).isNotEqualTo(antesCreate);
        assertThat(recebedorService.findById(criado.id()).id()).isEqualTo(criado.id());
    }

    /**
     * Monta um create DTO mínimo com os valores mais comuns.
     *
     * @param residenciaId residência obrigatória
     * @return DTO de cadastro
     */
    private RecebedorCreateDTO basico(UUID residenciaId) {
        return new RecebedorCreateDTO(
                "Bruno Lima", "55566677788", "", null,
                LocalDate.of(1985, 6, 15), "", "", false, "", "",
                false, false, null, false, "", residenciaId);
    }
}
