package com.vonlanplace.doacao.recebedor.auxilio;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
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
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link AuxilioService} contra o banco H2 real.
 *
 * <p>CRUD puro de sub-registro, mais a somatória por recebedor — a renda
 * assistencial que o entrevistador confronta com as despesas. A cascata também
 * vale o teste: o auxílio morre com o recebedor.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("AuxilioService")
class AuxilioServiceTest {

    @Autowired
    private AuxilioService auxilioService;

    @Autowired
    private AuxilioRepository auxilioRepository;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private EntityManager entityManager;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private Recebedor recebedor;

    /**
     * Cria o recebedor usado como vínculo dos auxílios.
     */
    @BeforeEach
    void setUp() {
        Residencia residencia = new Residencia();
        residencia.setLogradouro("Rua das Flores");
        residencia.setNumero("100");
        residencia.setCep("01001000");
        residencia.setCidade("Recife");
        residencia.setBairro("Santo Amaro");
        residencia.setNumMoradores(1);
        residencia.setNumTrabalhadores(0);
        residencia.setTipoOcupacao(TipoOcupacao.LOCACAO);
        residencia.setTipoResidencia(TipoResidencia.ALVENARIA_TRADICIONAL);
        residencia = residenciaRepository.save(residencia);

        recebedor = new Recebedor();
        recebedor.setNomeCompleto("Maria da Silva");
        recebedor.setCpf("11111111111");
        recebedor.setDataNascimento(LocalDate.of(1980, 5, 20));
        recebedor.setLeEscreve(true);
        recebedor.setResidencia(residencia);
        recebedor = recebedorRepository.save(recebedor);
    }

    @Test
    @DisplayName("cria auxílio ligado ao recebedor")
    void criaAuxilio() {
        AuxilioResponseDTO criado = auxilioService.create(
                new AuxilioCreateDTO(new BigDecimal("600.00"), "Bolsa Família", null, recebedor.getId()));

        assertThat(criado.id()).isNotNull();
        assertThat(criado.valor()).isEqualByComparingTo("600.00");
        assertThat(criado.fonte()).isEqualTo("Bolsa Família");
        assertThat(criado.recebedor().id()).isEqualTo(recebedor.getId());
    }

    @Test
    @DisplayName("recusa criação com recebedor inexistente")
    void recusaCriacaoComRecebedorInexistente() {
        assertThatThrownBy(() -> auxilioService.create(
                new AuxilioCreateDTO(new BigDecimal("100.00"), "ONG", null, UUID.randomUUID())))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("busca por id inexistente falha")
    void buscaPorIdInexistenteFalha() {
        assertThatThrownBy(() -> auxilioService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Auxilio not found");
    }

    @Test
    @DisplayName("lista paginada por recebedor")
    void listaPorRecebedor() {
        auxilioRepository.save(auxilio(recebedor, "600.00"));
        auxilioRepository.save(auxilio(recebedor, "150.00"));
        assertThat(auxilioService.findByRecebedor(recebedor.getId(), PAGINA).getTotalElements())
                .isEqualTo(2);
    }

    @Test
    @DisplayName("filtro por recebedor inexistente falha")
    void filtroPorRecebedorInexistenteFalha() {
        assertThatThrownBy(() -> auxilioService.findByRecebedor(UUID.randomUUID(), PAGINA))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("soma os auxílios do recebedor")
    void somaPorRecebedor() {
        auxilioRepository.save(auxilio(recebedor, "600.00"));
        auxilioRepository.save(auxilio(recebedor, "150.00"));

        assertThat(auxilioService.somaPorRecebedor(recebedor.getId()))
                .isEqualByComparingTo("750.00")
                .hasScaleOf(2);
    }

    @Test
    @DisplayName("soma retorna zero sem auxílios")
    void somaVaziaRetornaZero() {
        assertThat(auxilioService.somaPorRecebedor(recebedor.getId()))
                .isZero()
                .hasScaleOf(2);
    }

    @Test
    @DisplayName("soma de recebedor inexistente falha")
    void somaDeRecebedorInexistenteFalha() {
        assertThatThrownBy(() -> auxilioService.somaPorRecebedor(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("atualiza auxílio")
    void atualizaAuxilio() {
        Auxilio persistido = auxilioRepository.save(auxilio(recebedor, "600.00"));

        AuxilioResponseDTO atualizado = auxilioService.update(
                persistido.getId(),
                new AuxilioUpdateDTO(new BigDecimal("650.00"), "Bolsa Família", "Reajuste", recebedor.getId()));

        assertThat(atualizado.valor()).isEqualByComparingTo("650.00");
        assertThat(atualizado.descricao()).isEqualTo("Reajuste");
    }

    @Test
    @DisplayName("atualização de auxílio inexistente falha")
    void atualizaAuxilioInexistenteFalha() {
        assertThatThrownBy(() -> auxilioService.update(
                UUID.randomUUID(),
                new AuxilioUpdateDTO(new BigDecimal("100.00"), "ONG", null, recebedor.getId())))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("remove auxílio isolado")
    void removeAuxilio() {
        UUID id = auxilioRepository.save(auxilio(recebedor, "600.00")).getId();

        auxilioService.delete(id);

        assertThat(auxilioRepository.findById(id)).isEmpty();
        assertThat(recebedorRepository.findById(recebedor.getId())).isPresent();
    }

    @Test
    @DisplayName("auxílio morre em cascata com o recebedor")
    void auxilioMorreComRecebedor() {
        UUID auxilioId = auxilioRepository.save(auxilio(recebedor, "600.00")).getId();
        entityManager.flush();
        entityManager.clear();

        Recebedor recarregado = recebedorRepository.findById(recebedor.getId()).orElseThrow();
        recebedorRepository.delete(recarregado);
        entityManager.flush();
        entityManager.clear();

        assertThat(recebedorRepository.findById(recebedor.getId())).isEmpty();
        assertThat(auxilioRepository.findById(auxilioId)).isEmpty();
    }

    /**
     * Constrói um auxílio já persistido.
     *
     * @param recebedor recebedor de vínculo
     * @param valor     valor do auxílio
     * @return auxílio persistido
     */
    private Auxilio auxilio(Recebedor recebedor, String valor) {
        Auxilio auxilio = new Auxilio();
        auxilio.setValor(new BigDecimal(valor));
        auxilio.setFonte("Bolsa Família");
        auxilio.setRecebedor(recebedor);
        return auxilio;
    }
}