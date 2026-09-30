package com.vonlanplace.doacao.recebedor.dependente;

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

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link DependenteService} contra o banco H2 real.
 *
 * <p>CRUD puro de sub-registro. O que vale o banco é o vínculo com o recebedor
 * (derivado {@code findByRecebedor_Id}) e a cascata: o dependente morre junto
 * com o recebedor, e o {@code delete} isolado o remove sozinho.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("DependenteService")
class DependenteServiceTest {

    @Autowired
    private DependenteService dependenteService;

    @Autowired
    private DependenteRepository dependenteRepository;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private EntityManager entityManager;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private Recebedor recebedor;

    /**
     * Cria o recebedor usado como vínculo dos dependentes.
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
    @DisplayName("cria dependente ligado ao recebedor")
    void criaDependente() {
        DependenteResponseDTO criado = dependenteService.create(
                new DependenteCreateDTO("Ana da Silva", 7, LocalDate.of(2017, 3, 10), recebedor.getId()));

        assertThat(criado.id()).isNotNull();
        assertThat(criado.nome()).isEqualTo("Ana da Silva");
        assertThat(criado.idade()).isEqualTo(7);
        assertThat(criado.recebedor().id()).isEqualTo(recebedor.getId());
    }

    @Test
    @DisplayName("recusa criação com recebedor inexistente")
    void recusaCriacaoComRecebedorInexistente() {
        assertThatThrownBy(() -> dependenteService.create(
                new DependenteCreateDTO("Ana", 7, null, UUID.randomUUID())))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recebedor not found");
    }

    @Test
    @DisplayName("busca por id inexistente falha")
    void buscaPorIdInexistenteFalha() {
        assertThatThrownBy(() -> dependenteService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Dependente not found");
    }

    @Test
    @DisplayName("lista só os dependentes do recebedor, sem paginação")
    void listaPorRecebedor() {
        dependenteRepository.save(dependente(recebedor, "Ana", 7));
        dependenteRepository.save(dependente(recebedor, "Bruno", 5));
        assertThat(dependenteService.findByRecebedor(recebedor.getId()))
                .extracting(DependenteResponseDTO::nome)
                .containsExactlyInAnyOrder("Ana", "Bruno");
    }

    @Test
    @DisplayName("lista paginada por recebedor")
    void listaPaginaPorRecebedor() {
        dependenteRepository.save(dependente(recebedor, "Ana", 7));
        assertThat(dependenteService.findByRecebedor(recebedor.getId(), PAGINA).getTotalElements())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("filtro por recebedor inexistente falha")
    void filtroPorRecebedorInexistenteFalha() {
        assertThatThrownBy(() -> dependenteService.findByRecebedor(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("atualiza dependente")
    void atualizaDependente() {
        Dependente persistido = dependenteRepository.save(dependente(recebedor, "Ana", 7));

        DependenteResponseDTO atualizado = dependenteService.update(
                persistido.getId(),
                new DependenteUpdateDTO("Ana Maria", 8, LocalDate.of(2016, 3, 10), recebedor.getId()));

        assertThat(atualizado.nome()).isEqualTo("Ana Maria");
        assertThat(atualizado.idade()).isEqualTo(8);
    }

    @Test
    @DisplayName("remove dependente isolado")
    void removeDependente() {
        Dependente persistido = dependenteRepository.save(dependente(recebedor, "Ana", 7));
        UUID id = persistido.getId();

        dependenteService.delete(id);

        assertThat(dependenteRepository.findById(id)).isEmpty();
        assertThat(recebedorRepository.findById(recebedor.getId())).isPresent();
    }

    @Test
    @DisplayName("dependente morre em cascata com o recebedor")
    void dependenteMorreComRecebedor() {
        UUID dependenteId = dependenteRepository.save(dependente(recebedor, "Ana", 7)).getId();
        entityManager.flush();
        entityManager.clear();

        Recebedor recarregado = recebedorRepository.findById(recebedor.getId()).orElseThrow();
        recebedorRepository.delete(recarregado);
        entityManager.flush();
        entityManager.clear();

        assertThat(recebedorRepository.findById(recebedor.getId())).isEmpty();
        assertThat(dependenteRepository.findById(dependenteId)).isEmpty();
    }

    /**
     * Constrói um dependente já persistido.
     *
     * @param recebedor recebedor de vínculo
     * @param nome      nome do dependente
     * @param idade     idade do dependente
     * @return dependente persistido
     */
    private Dependente dependente(Recebedor recebedor, String nome, int idade) {
        Dependente dependente = new Dependente();
        dependente.setNome(nome);
        dependente.setIdade(idade);
        dependente.setRecebedor(recebedor);
        return dependente;
    }
}