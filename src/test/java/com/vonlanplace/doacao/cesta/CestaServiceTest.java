package com.vonlanplace.doacao.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
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
 * Testes de integração do {@link CestaService} contra o banco H2 real.
 *
 * <p><strong>Suíte curta de propósito, mas com dois casos que valem o banco.</strong> A
 * cesta é CRUD com recebedor opcional, sem lista de itens, então não há o que testar de
 * estrutura além do vínculo. O que não dá para testar por stub é a assimetria entre os
 * dois campos opcionais na atualização: o recebedor é resolvido à mão e um {@code null}
 * <strong>desliga</strong> a cesta, enquanto a data de entrega passa pelo
 * {@code nullValuePropertyMappingStrategy.IGNORE} e um {@code null} <strong>mantém</strong>
 * o valor anterior. Os dois casos abaixo fixam exatamente essa diferença — é a única
 * parte do serviço que um teste de memória deixaria passar errado.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("CestaService")
class CestaServiceTest {

    @Autowired
    private CestaService cestaService;

    @Autowired
    private CestaRepository cestaRepository;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private Recebedor recebedor;

    /**
     * Cria o recebedor usado para testar o vínculo opcional.
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

    /**
     * Cria uma cesta já persistida e devolve seu id.
     *
     * @param dataEntrega data da entrega
     * @return identificador da cesta criada
     */
    private UUID criaCesta(LocalDate dataEntrega) {
        Cesta cesta = new Cesta();
        cesta.setDataEntrega(dataEntrega);
        return cestaRepository.save(cesta).getId();
    }

    @Test
    @DisplayName("cria cesta com recebedor")
    void criaCestaComRecebedor() {
        CestaResponseDTO criada = cestaService.create(
                new CestaCreateDTO(LocalDate.of(2024, 2, 3), recebedor.getId()));

        assertThat(criada.id()).isNotNull();
        assertThat(criada.dataEntrega()).isEqualTo(LocalDate.of(2024, 2, 3));
        assertThat(criada.recebedor().id()).isEqualTo(recebedor.getId());
    }

    @Test
    @DisplayName("cria cesta sem recebedor e sem data")
    void criaCestaSemVinculos() {
        CestaResponseDTO criada = cestaService.create(new CestaCreateDTO(null, null));

        assertThat(criada.id()).isNotNull();
        assertThat(criada.dataEntrega()).isNull();
        assertThat(criada.recebedor()).isNull();
    }

    @Test
    @DisplayName("recusa criação com recebedor inexistente")
    void recusaCriacaoComRecebedorInexistente() {
        assertThatThrownBy(() -> cestaService.create(
                new CestaCreateDTO(LocalDate.of(2024, 2, 3), UUID.randomUUID())))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recebedor not found: ");
    }

    @Test
    @DisplayName("busca por id aninha o recebedor")
    void buscaPorId() {
        UUID id = cestaService.create(
                new CestaCreateDTO(LocalDate.of(2024, 2, 3), recebedor.getId())).id();

        CestaResponseDTO encontrada = cestaService.findById(id);

        assertThat(encontrada.dataEntrega()).isEqualTo(LocalDate.of(2024, 2, 3));
        assertThat(encontrada.recebedor().nomeCompleto()).isEqualTo("Maria da Silva");
    }

    @Test
    @DisplayName("busca por id inexistente falha")
    void buscaPorIdInexistenteFalha() {
        assertThatThrownBy(() -> cestaService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cesta not found: ");
    }

    @Test
    @DisplayName("lista todas as cestas")
    void listaTodas() {
        criaCesta(LocalDate.of(2024, 1, 1));
        criaCesta(LocalDate.of(2024, 2, 3));

        assertThat(cestaService.findAll(PAGINA).getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("atualização mantém a data anterior quando o campo é omitido")
    void atualizacaoMantemDataOmitida() {
        UUID id = cestaService.create(
                new CestaCreateDTO(LocalDate.of(2024, 2, 3), recebedor.getId())).id();

        CestaResponseDTO atualizada = cestaService.update(id, new CestaUpdateDTO(null, null));

        assertThat(atualizada.dataEntrega()).isEqualTo(LocalDate.of(2024, 2, 3));
        assertThat(atualizada.recebedor()).isNull();
    }

    @Test
    @DisplayName("atualização desliga o recebedor quando o id é omitido")
    void atualizacaoDesligaRecebedor() {
        UUID id = cestaService.create(
                new CestaCreateDTO(LocalDate.of(2024, 2, 3), recebedor.getId())).id();

        CestaResponseDTO atualizada = cestaService.update(id, new CestaUpdateDTO(null, null));

        assertThat(cestaRepository.findById(id).orElseThrow().getRecebedor()).isNull();
    }

    @Test
    @DisplayName("atualização troca o recebedor e a data")
    void atualizacaoTrocaRecebedorEData() {
        Recebedor outro = new Recebedor();
        outro.setNomeCompleto("João da Costa");
        outro.setCpf("22222222222");
        outro.setDataNascimento(LocalDate.of(1990, 1, 1));
        outro.setLeEscreve(false);
        outro.setResidencia(recebedor.getResidencia());
        outro = recebedorRepository.save(outro);

        UUID id = cestaService.create(
                new CestaCreateDTO(LocalDate.of(2024, 2, 3), recebedor.getId())).id();

        CestaResponseDTO atualizada = cestaService.update(
                id, new CestaUpdateDTO(LocalDate.of(2024, 5, 6), outro.getId()));

        assertThat(atualizada.dataEntrega()).isEqualTo(LocalDate.of(2024, 5, 6));
        assertThat(atualizada.recebedor().id()).isEqualTo(outro.getId());
    }

    @Test
    @DisplayName("recusa atualização com recebedor inexistente")
    void recusaAtualizacaoComRecebedorInexistente() {
        UUID id = criaCesta(LocalDate.of(2024, 2, 3));

        assertThatThrownBy(() -> cestaService.update(
                id, new CestaUpdateDTO(null, UUID.randomUUID())))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recebedor not found: ");
    }

    @Test
    @DisplayName("atualização de cesta inexistente falha")
    void atualizacaoDeCestaInexistenteFalha() {
        assertThatThrownBy(() -> cestaService.update(
                UUID.randomUUID(), new CestaUpdateDTO(null, null)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cesta not found: ");
    }

    @Test
    @DisplayName("remove cesta")
    void removeCesta() {
        UUID id = criaCesta(LocalDate.of(2024, 2, 3));

        cestaService.delete(id);

        assertThat(cestaRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("remoção de cesta inexistente falha")
    void removeCestaInexistenteFalha() {
        assertThatThrownBy(() -> cestaService.delete(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Cesta not found: ");
    }
}
