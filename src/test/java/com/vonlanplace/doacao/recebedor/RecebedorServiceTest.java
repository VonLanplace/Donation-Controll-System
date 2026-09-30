package com.vonlanplace.doacao.recebedor;

import com.vonlanplace.doacao.cesta.Cesta;
import com.vonlanplace.doacao.cesta.CestaRepository;
import com.vonlanplace.doacao.doacao.Doacao;
import com.vonlanplace.doacao.doacao.DoacaoRepository;
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
 * Testes de integração do {@link RecebedorService} contra o banco H2 real.
 *
 * <p><strong>O foco são os guards de chave duplicada.</strong> CPF e PIX têm índice único
 * no banco, mas o serviço barra a duplicidade antes de gravar, para devolver 409 com
 * mensagem específica em vez de violação de constraint. Esses dois guards recebem o id a
 * desconsiderar justamente para a atualização não conflitar consigo mesma, e é esse
 * detalhe — passar o id certo ou {@code null} — que os casos de atualização cobrem.</p>
 *
 * <p><strong>As duas travas de exclusão também são testadas de verdade.</strong> Doação e
 * cesta são inseridas direto pelo repositório, porque o que se quer exercitar é o
 * {@code existsByRecebedor} de cada um, não o serviço que os cria.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("RecebedorService")
class RecebedorServiceTest {

    @Autowired
    private RecebedorService recebedorService;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private DoacaoRepository doacaoRepository;

    @Autowired
    private CestaRepository cestaRepository;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private Residencia residencia;
    private Recebedor recebedor;

    /**
     * Cria a residência e o recebedor que servem de base.
     */
    @BeforeEach
    void setUp() {
        Residencia nova = new Residencia();
        nova.setLogradouro("Rua das Flores");
        nova.setNumero("100");
        nova.setCep("01001000");
        nova.setCidade("Recife");
        nova.setBairro("Santo Amaro");
        nova.setNumMoradores(1);
        nova.setNumTrabalhadores(0);
        nova.setTipoOcupacao(TipoOcupacao.LOCACAO);
        nova.setTipoResidencia(TipoResidencia.ALVENARIA_TRADICIONAL);
        residencia = residenciaRepository.save(nova);

        Recebedor novo = new Recebedor();
        novo.setNomeCompleto("Maria da Silva");
        novo.setCpf("11111111111");
        novo.setDataNascimento(LocalDate.of(1980, 5, 20));
        novo.setLeEscreve(true);
        novo.setPix("maria@pix.com");
        novo.setResidencia(residencia);
        recebedor = recebedorRepository.save(novo);
    }

    /**
     * Monta um DTO de criação válido, com a residência base e chaves livres.
     *
     * @return DTO pronto para uso
     */
    private RecebedorCreateDTO dtoDeCriacao() {
        return new RecebedorCreateDTO(
                "João da Costa", "22222222222", "12345", OrgaoEmissorRG.SSP,
                LocalDate.of(1990, 3, 10), "81999999999", null,
                true, "joao@email.com", "joao@pix.com",
                false, false, false, false, "observação", residencia.getId());
    }

    @Test
    @DisplayName("cria recebedor e liga à residência informada")
    void criaRecebedor() {
        RecebedorResponseDTO criado = recebedorService.create(dtoDeCriacao());

        assertThat(criado.id()).isNotNull();
        assertThat(criado.cpf()).isEqualTo("22222222222");
        assertThat(criado.residencia().id()).isEqualTo(residencia.getId());
    }

    @Test
    @DisplayName("recusa criação com residence inexistente")
    void recusaCriacaoComResidenciaInexistente() {
        RecebedorCreateDTO dto = new RecebedorCreateDTO(
                "Outro", "99988877766", null, null,
                LocalDate.of(1990, 1, 1), null, null,
                true, null, null, false, false, false, false, null, UUID.randomUUID());

        assertThatThrownBy(() -> recebedorService.create(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Residencia not found");
    }

    @Test
    @DisplayName("recusa criação com CPF já cadastrado")
    void recusaCpfDuplicado() {
        RecebedorCreateDTO duplicado = new RecebedorCreateDTO(
                "Outra Pessoa", "11111111111", null, null,
                LocalDate.of(1990, 1, 1), null, null,
                true, null, null, false, false, false, false, null, residencia.getId());

        assertThatThrownBy(() -> recebedorService.create(duplicado))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("CPF já cadastrado para outro recebedor");
    }

    @Test
    @DisplayName("recusa criação com chave PIX já cadastrada")
    void recusaPixDuplicado() {
        RecebedorCreateDTO duplicado = new RecebedorCreateDTO(
                "Outra Pessoa", "99988877766", null, null,
                LocalDate.of(1990, 1, 1), null, null,
                true, null, "maria@pix.com", false, false, false, false, null, residencia.getId());

        assertThatThrownBy(() -> recebedorService.create(duplicado))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Chave PIX já cadastrada para outro recebedor");
    }

    @Test
    @DisplayName("chave PIX vazia não entra no guard de duplicidade")
    void pixVazioNaoConfere() {
        RecebedorResponseDTO criado = recebedorService.create(new RecebedorCreateDTO(
                "Outra Pessoa", "99988877766", null, null,
                LocalDate.of(1990, 1, 1), null, null,
                true, null, "   ", false, false, false, false, null, residencia.getId()));

        assertThat(criado.pix()).isEqualTo("   ");
    }

    @Test
    @DisplayName("busca por id devolve a residência aninhada")
    void buscaPorIdAninhaResidencia() {
        RecebedorResponseDTO encontrado = recebedorService.findById(recebedor.getId());

        assertThat(encontrado.nomeCompleto()).isEqualTo("Maria da Silva");
        assertThat(encontrado.residencia().logradouro()).isEqualTo("Rua das Flores");
    }

    @Test
    @DisplayName("busca por id inexistente falha")
    void buscaPorIdInexistenteFalha() {
        assertThatThrownBy(() -> recebedorService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recebedor not found: ");
    }

    @Test
    @DisplayName("busca por CPF e por PIX")
    void buscaPorDocumentos() {
        assertThat(recebedorService.findByCpf("11111111111").id()).isEqualTo(recebedor.getId());
        assertThat(recebedorService.findByPix("maria@pix.com").id()).isEqualTo(recebedor.getId());
    }

    @Test
    @DisplayName("busca por CPF inexistente falha")
    void buscaPorCpfInexistenteFalha() {
        assertThatThrownBy(() -> recebedorService.findByCpf("00000000000"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recebedor not found by cpf: ");
    }

    @Test
    @DisplayName("busca por PIX inexistente falha")
    void buscaPorPixInexistenteFalha() {
        assertThatThrownBy(() -> recebedorService.findByPix("ninguem@email.com"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recebedor not found by pix: ");
    }

    @Test
    @DisplayName("busca por nome completo, data de nascimento e residência")
    void buscaPorOutrosCampos() {
        assertThat(recebedorService.findByNomeCompleto("Maria da Silva", PAGINA).getTotalElements())
                .isEqualTo(1);
        assertThat(recebedorService.findByDataNascimento(LocalDate.of(1980, 5, 20), PAGINA)
                .getTotalElements()).isEqualTo(1);
        assertThat(recebedorService.findByResidencia(residencia.getId(), PAGINA).getTotalElements())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("busca por residência valida a residência antes e devolve 404 se não existir")
    void buscaPorResidenciaInexistenteFalha() {
        assertThatThrownBy(() -> recebedorService.findByResidencia(UUID.randomUUID(), PAGINA))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Residencia not found");
    }

    @Test
    @DisplayName("atualização com o mesmo CPF não entra em conflito consigo mesma")
    void atualizacaoComMesmoCpfNaoConflita() {
        RecebedorResponseDTO atualizado = recebedorService.update(
                recebedor.getId(),
                new RecebedorUpdateDTO(
                        "Maria Souza", "11111111111", null, null,
                        LocalDate.of(1980, 5, 20), null, null,
                        true, null, "nova@pix.com", false, false, false, false, null, residencia.getId()));

        assertThat(atualizado.nomeCompleto()).isEqualTo("Maria Souza");
        assertThat(atualizado.pix()).isEqualTo("nova@pix.com");
    }

    @Test
    @DisplayName("recusa atualização para CPF de outro recebedor")
    void recusaAtualizacaoParaCpfDeOutro() {
        Recebedor outro = new Recebedor();
        outro.setNomeCompleto("João");
        outro.setCpf("55566677788");
        outro.setDataNascimento(LocalDate.of(1975, 2, 2));
        outro.setLeEscreve(false);
        outro.setResidencia(residencia);
        recebedorRepository.save(outro);

        assertThatThrownBy(() -> recebedorService.update(
                recebedor.getId(),
                new RecebedorUpdateDTO(
                        "Maria Souza", "55566677788", null, null,
                        LocalDate.of(1980, 5, 20), null, null,
                        true, null, null, false, false, false, false, null, residencia.getId())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("CPF já cadastrado para outro recebedor");
    }

    @Test
    @DisplayName("recusa atualização para PIX de outro recebedor")
    void recusaAtualizacaoParaPixDeOutro() {
        Recebedor outro = new Recebedor();
        outro.setNomeCompleto("João");
        outro.setCpf("55566677788");
        outro.setDataNascimento(LocalDate.of(1975, 2, 2));
        outro.setLeEscreve(false);
        outro.setPix("joao@pix.com");
        outro.setResidencia(residencia);
        recebedorRepository.save(outro);

        assertThatThrownBy(() -> recebedorService.update(
                recebedor.getId(),
                new RecebedorUpdateDTO(
                        "Maria Souza", "11111111111", null, null,
                        LocalDate.of(1980, 5, 20), null, null,
                        true, null, "joao@pix.com", false, false, false, false, null, residencia.getId())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Chave PIX já cadastrada para outro recebedor");
    }

    @Test
    @DisplayName("atualização de recebedor inexistente falha")
    void atualizacaoDeRecebedorInexistenteFalha() {
        assertThatThrownBy(() -> recebedorService.update(
                UUID.randomUUID(),
                new RecebedorUpdateDTO(
                        "Fantasma", "00000000000", null, null,
                        LocalDate.of(1980, 1, 1), null, null,
                        true, null, null, false, false, false, false, null, residencia.getId())))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("remove recebedor sem doações nem cestas")
    void removeRecebedor() {
        UUID id = recebedor.getId();
        recebedorService.delete(id);

        assertThat(recebedorRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("recusa remover recebedor com doação vinculada")
    void recusaRemoverRecebedorComDoacao() {
        Doacao doacao = new Doacao();
        doacao.setData(LocalDate.of(2024, 1, 10));
        doacao.setRecebedor(recebedor);
        doacaoRepository.save(doacao);

        assertThatThrownBy(() -> recebedorService.delete(recebedor.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Não é possível excluir recebedor com doações vinculadas");
        assertThat(recebedorRepository.findById(recebedor.getId())).isPresent();
    }

    @Test
    @DisplayName("recusa remover recebedor com cesta vinculada")
    void recusaRemoverRecebedorComCesta() {
        Cesta cesta = new Cesta();
        cesta.setDataEntrega(LocalDate.of(2024, 2, 3));
        cesta.setRecebedor(recebedor);
        cestaRepository.save(cesta);

        assertThatThrownBy(() -> recebedorService.delete(recebedor.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Não é possível excluir recebedor com cestas vinculadas");
        assertThat(recebedorRepository.findById(recebedor.getId())).isPresent();
    }

    @Test
    @DisplayName("remover recebedor inexistente falha")
    void removeRecebedorInexistenteFalha() {
        assertThatThrownBy(() -> recebedorService.delete(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
