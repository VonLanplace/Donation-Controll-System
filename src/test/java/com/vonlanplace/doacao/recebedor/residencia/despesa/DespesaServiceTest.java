package com.vonlanplace.doacao.recebedor.residencia.despesa;

import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link DespesaService} contra o banco H2 real.
 *
 * <p>CRUD puro de sub-registro, mais a somatória por residência — o valor que o
 * entrevistador usa para decidir a cesta. A cascata também vale o teste: a
 * despesa morre com a residência.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("DespesaService")
class DespesaServiceTest {

    @Autowired
    private DespesaService despesaService;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private EntityManager entityManager;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private Residencia residencia;

    /**
     * Cria a residência usada como vínculo das despesas.
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
    @DisplayName("cria despesa ligada à residência")
    void criaDespesa() {
        DespesaResponseDTO criada = despesaService.create(
                new DespesaCreateDTO(new BigDecimal("1250.00"), "Aluguel", "Casa alugada", residencia.getId()));

        assertThat(criada.id()).isNotNull();
        assertThat(criada.valor()).isEqualByComparingTo("1250.00");
        assertThat(criada.fonte()).isEqualTo("Aluguel");
        assertThat(criada.residencia().id()).isEqualTo(residencia.getId());
    }

    @Test
    @DisplayName("recusa criação com residência inexistente")
    void recusaCriacaoComResidenciaInexistente() {
        assertThatThrownBy(() -> despesaService.create(
                new DespesaCreateDTO(new BigDecimal("100.00"), "Luz", null, UUID.randomUUID())))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("busca por id inexistente falha")
    void buscaPorIdInexistenteFalha() {
        assertThatThrownBy(() -> despesaService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Despesa not found");
    }

    @Test
    @DisplayName("lista paginada por residência")
    void listaPorResidencia() {
        despesaRepository.save(despesa(residencia, "90.00"));
        despesaRepository.save(despesa(residencia, "10.00"));
        assertThat(despesaService.findByResidencia(residencia.getId(), PAGINA).getTotalElements())
                .isEqualTo(2);
    }

    @Test
    @DisplayName("filtro por residência inexistente falha")
    void filtroPorResidenciaInexistenteFalha() {
        assertThatThrownBy(() -> despesaService.findByResidencia(UUID.randomUUID(), PAGINA))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("soma as despesas da residência")
    void somaPorResidencia() {
        despesaRepository.save(despesa(residencia, "1250.00"));
        despesaRepository.save(despesa(residencia, "90.00"));
        despesaRepository.save(despesa(residencia, "10.00"));

        assertThat(despesaService.somaPorResidencia(residencia.getId()))
                .isEqualByComparingTo("1350.00")
                .hasScaleOf(2);
    }

    @Test
    @DisplayName("soma retorna zero sem despesas")
    void somaVaziaRetornaZero() {
        assertThat(despesaService.somaPorResidencia(residencia.getId()))
                .isZero()
                .hasScaleOf(2);
    }

    @Test
    @DisplayName("soma de residência inexistente falha")
    void somaDeResidenciaInexistenteFalha() {
        assertThatThrownBy(() -> despesaService.somaPorResidencia(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("atualiza despesa")
    void atualizaDespesa() {
        Despesa persistida = despesaRepository.save(despesa(residencia, "1250.00"));

        DespesaResponseDTO atualizada = despesaService.update(
                persistida.getId(),
                new DespesaUpdateDTO(new BigDecimal("1300.00"), "Aluguel", "Reajuste anual", residencia.getId()));

        assertThat(atualizada.valor()).isEqualByComparingTo("1300.00");
        assertThat(atualizada.descricao()).isEqualTo("Reajuste anual");
    }

    @Test
    @DisplayName("atualização de despesa inexistente falha")
    void atualizaDespesaInexistenteFalha() {
        assertThatThrownBy(() -> despesaService.update(
                UUID.randomUUID(),
                new DespesaUpdateDTO(new BigDecimal("100.00"), "Luz", null, residencia.getId())))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("remove despesa isolada")
    void removeDespesa() {
        UUID id = despesaRepository.save(despesa(residencia, "90.00")).getId();

        despesaService.delete(id);

        assertThat(despesaRepository.findById(id)).isEmpty();
        assertThat(residenciaRepository.findById(residencia.getId())).isPresent();
    }

    @Test
    @DisplayName("despesa morre em cascata com a residência")
    void despesaMorreComResidencia() {
        UUID despesaId = despesaRepository.save(despesa(residencia, "90.00")).getId();
        entityManager.flush();
        entityManager.clear();

        Residencia recarregada = residenciaRepository.findById(residencia.getId()).orElseThrow();
        residenciaRepository.delete(recarregada);
        entityManager.flush();
        entityManager.clear();

        assertThat(residenciaRepository.findById(residencia.getId())).isEmpty();
        assertThat(despesaRepository.findById(despesaId)).isEmpty();
    }

    /**
     * Constrói uma despesa já persistida.
     *
     * @param residencia residência de vínculo
     * @param valor      valor da despesa
     * @return despesa persistida
     */
    private Despesa despesa(Residencia residencia, String valor) {
        Despesa despesa = new Despesa();
        despesa.setValor(new BigDecimal(valor));
        despesa.setFonte("Aluguel");
        despesa.setResidencia(residencia);
        return despesa;
    }
}