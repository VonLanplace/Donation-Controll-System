package com.vonlanplace.doacao.recebedor.residencia;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link ResidenciaService} contra o banco H2 real.
 *
 * <p><strong>Suíte mais curta do projeto, e isso é proposital.</strong> O serviço é CRUD
 * puro por id, sem guarda de exclusão e sem chave única. O que ainda exige banco real são
 * as quatro buscas por campo do endereço, porque cada uma depende do nome derivado no
 * {@code ResidenciaRepository}.</p>
 *
 * <p><strong>A exclusão com moradores não é testada aqui de propósito.</strong> O serviço
 * não checa nada, e o comportamento real — violação de FK traduzida em 409 genérico pelo
 * {@code GlobalExceptionHandler} — acontece dentro do handler, não deste serviço. Fixa-lo
 * aqui seria testar o descaso.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("ResidenciaService")
class ResidenciaServiceTest {

    @Autowired
    private ResidenciaService residenciaService;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private Residencia residencia;

    /**
     * Cria a residência usada como base.
     */
    @BeforeEach
    void setUp() {
        Residencia nova = new Residencia();
        nova.setLogradouro("Rua das Flores");
        nova.setNumero("100");
        nova.setCep("01001000");
        nova.setCidade("Recife");
        nova.setBairro("Santo Amaro");
        nova.setNumMoradores(4);
        nova.setNumTrabalhadores(1);
        nova.setTipoOcupacao(TipoOcupacao.LOCACAO);
        nova.setTipoResidencia(TipoResidencia.ALVENARIA_TRADICIONAL);
        residencia = residenciaRepository.save(nova);
    }

    @Test
    @DisplayName("cria residência")
    void criaResidencia() {
        ResidenciaResponseDTO criada = residenciaService.create(
                new ResidenciaCreateDTO(
                        "Avenida Brasil", "500", "20001000", null,
                        "Rio de Janeiro", "Centro", 3, 2,
                        TipoOcupacao.PROPRIEDADE, TipoResidencia.DRYWALL));

        assertThat(criada.id()).isNotNull();
        assertThat(criada.logradouro()).isEqualTo("Avenida Brasil");
        assertThat(criada.tipoOcupacao()).isEqualTo(TipoOcupacao.PROPRIEDADE);
    }

    @Test
    @DisplayName("busca por id devolve todos os campos")
    void buscaPorId() {
        ResidenciaResponseDTO encontrada = residenciaService.findById(residencia.getId());

        assertThat(encontrada.logradouro()).isEqualTo("Rua das Flores");
        assertThat(encontrada.numMoradores()).isEqualTo(4);
        assertThat(encontrada.numTrabalhadores()).isEqualTo(1);
        assertThat(encontrada.tipoResidencia()).isEqualTo(TipoResidencia.ALVENARIA_TRADICIONAL);
    }

    @Test
    @DisplayName("busca por id inexistente falha")
    void buscaPorIdInexistenteFalha() {
        assertThatThrownBy(() -> residenciaService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Residencia not found");
    }

    @Test
    @DisplayName("resposta não inclui a lista de moradores, apesar de a entidade mantê-la")
    void respostaNaoIncluiMoradores() {
        ResidenciaResponseDTO encontrada = residenciaService.findById(residencia.getId());

        assertThat(residencia.getRecebedores()).isEmpty();
        assertThat(ResidenciaResponseDTO.class.getRecordComponents())
                .noneMatch(componente -> componente.getName().equals("recebedores"));
    }

    @Test
    @DisplayName("lista todas as residências")
    void listaTodas() {
        assertThat(residenciaService.findAll(PAGINA).getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("busca por logradouro exato")
    void buscaPorLogradouro() {
        assertThat(residenciaService.findByLogradouro("Rua das Flores", PAGINA).getTotalElements())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("busca por CEP exato")
    void buscaPorCep() {
        assertThat(residenciaService.findByCep("01001000", PAGINA).getTotalElements())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("busca por cidade exata")
    void buscaPorCidade() {
        assertThat(residenciaService.findByCidade("Recife", PAGINA).getTotalElements())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("busca por bairro exato")
    void buscaPorBairro() {
        assertThat(residenciaService.findByBairro("Santo Amaro", PAGINA).getTotalElements())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("busca não é por fragmento: outro logradouro não encontra")
    void buscaEhExataNaoParcial() {
        assertThat(residenciaService.findByLogradouro("das Flores", PAGINA).getTotalElements())
                .isZero();
    }

    @Test
    @DisplayName("atualiza residência")
    void atualizaResidencia() {
        ResidenciaResponseDTO atualizada = residenciaService.update(
                residencia.getId(),
                new ResidenciaUpdateDTO(
                        "Rua das Flores", "100A", "01001000", "Bloco B",
                        "Recife", "Santo Amaro", 5, 2,
                        TipoOcupacao.COMODATO, TipoResidencia.MADEIRA));

        assertThat(atualizada.numero()).isEqualTo("100A");
        assertThat(atualizada.complemento()).isEqualTo("Bloco B");
        assertThat(atualizada.tipoOcupacao()).isEqualTo(TipoOcupacao.COMODATO);
    }

    @Test
    @DisplayName("atualização de residência inexistente falha")
    void atualizaResidenciaInexistenteFalha() {
        assertThatThrownBy(() -> residenciaService.update(
                UUID.randomUUID(),
                new ResidenciaUpdateDTO(
                        "Rua", "1", "01001000", null, "Recife", "Centro", 1, 1,
                        TipoOcupacao.POSSE, TipoResidencia.METALICA)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("remove residência sem moradores")
    void removeResidencia() {
        UUID id = residencia.getId();
        residenciaService.delete(id);

        assertThat(residenciaRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("remover residência inexistente falha")
    void removeResidenciaInexistenteFalha() {
        assertThatThrownBy(() -> residenciaService.delete(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
