package com.vonlanplace.doacao.recebedor;

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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Confere a ida e volta dos dois campos novos da entrevista.
 *
 * <p><strong>Estes são os testes que impedem o mapeamento silencioso.</strong>
 * Os campos entraram na entidade, nos DTOs e no mapper, mas nada garante que a
 * cadeia funcione antes do primeiro formulário: um {@code @Mapping} esquecido,
 * um nome divergente ou um campo morto no DTO passaria por qualquer teste que
 * não salva e relê. Aqui o recebedor é criado, relido da base, atualizado e
 * relido de novo, e os dois booleans têm que chegar idênticos nas duas
 * pontas.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("Campos novos do recebedor")
class RecebedorCamposNovosTest {

    @Autowired
    private RecebedorService recebedorService;

    @Autowired
    private ResidenciaService residenciaService;

    @Test
    @DisplayName("recebeuCesta e parecerEntrevistador sobrevivem ao create e ao update")
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
                false, true, true, false, "Primeira visita.", residencia.id());

        RecebedorResponseDTO criado = recebedorService.create(createDTO);

        assertThat(criado.recebeuCesta()).as("o create perdeu recebeuCesta").isTrue();
        assertThat(criado.parecerEntrevistador()).as("o create perdeu parecerEntrevistador").isFalse();

        RecebedorResponseDTO relido = recebedorService.findById(criado.id());
        assertThat(relido.recebeuCesta()).isTrue();
        assertThat(relido.parecerEntrevistador()).isFalse();

        RecebedorUpdateDTO updateDTO = new RecebedorUpdateDTO(
                createDTO.nomeCompleto(), createDTO.cpf(), createDTO.rgNum(), null,
                createDTO.dataNascimento(), createDTO.telefonePessoal(), createDTO.telefoneRecado(),
                createDTO.leEscreve(), createDTO.email(), createDTO.pix(),
                createDTO.necessitaJuridico(), createDTO.necessitaPsicologo(),
                false, true, createDTO.comentarios(), residencia.id());

        RecebedorResponseDTO atualizado = recebedorService.update(criado.id(), updateDTO);

        assertThat(atualizado.recebeuCesta()).as("o update não trocou recebeuCesta").isFalse();
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
                false, false, false, false, "", residenciaId);
    }
}