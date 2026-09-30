package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.entrevistador.Entrevistador;
import com.vonlanplace.doacao.entrevistador.EntrevistadorRepository;
import com.vonlanplace.doacao.entrevistador.EntrevistadorResponseDTO;
import com.vonlanplace.doacao.produto.Produto;
import com.vonlanplace.doacao.produto.ProdutoRepository;
import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoRepository;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoRepository;
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

import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link DoacaoService} contra o banco H2 real.
 *
 * <p><strong>Esta é a suíte que mais justifica existir.</strong> A chave composta de
 * {@code DoacaoProduto} e a troca da lista inteira em
 * {@code DoacaoService.replaceProdutos} só podem ser exercitadas contra banco de
 * verdade. Em especial, o {@code flush} entre {@code deleteAll} e o
 * {@code saveAll}/{@code addAll}: sem ele, a atualização com os mesmos produtos quebraria
 * por chave duplicada — e o caso "atualizar com a mesma lista de produtos" abaixo fixa
 * exatamente esse comportamento.</p>
 *
 * <p><strong>O caso de produto repetido é o que o guard de {@code Set} protege.</strong> A
 * chave composta não admite o mesmo produto duas vezes na mesma doação, mas sem o
 * {@code IllegalStateException} o cliente receberia a mensagem genérica de violação de
 * constraint em vez da específica.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("DoacaoService")
class DoacaoServiceTest {

    @Autowired
    private DoacaoService doacaoService;

    @Autowired
    private DoacaoRepository doacaoRepository;

    @Autowired
    private DoacaoProdutoRepository doacaoProdutoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MarcaProdutoRepository marcaProdutoRepository;

    @Autowired
    private TipoProdutoRepository tipoProdutoRepository;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private EntrevistadorRepository entrevistadorRepository;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private Produto produtoA;
    private Produto produtoB;
    private Recebedor recebedor;
    private Entrevistador entrevistador;

    /**
     * Cria os produtos, o recebedor e o entrevistador usados pelos casos.
     */
    @BeforeEach
    void setUp() {
        MarcaProduto marca = new MarcaProduto();
        marca.setNome("Marca Teste");
        marca = marcaProdutoRepository.save(marca);

        TipoProduto tipo = new TipoProduto();
        tipo.setNome("Tipo Teste");
        tipo = tipoProdutoRepository.save(tipo);

        produtoA = new Produto();
        produtoA.setCodigoBarras("7891000100103");
        produtoA.setMarcaProduto(marca);
        produtoA.setTipoProduto(tipo);
        produtoA = produtoRepository.save(produtoA);

        produtoB = new Produto();
        produtoB.setCodigoBarras("7891000100107");
        produtoB.setMarcaProduto(marca);
        produtoB.setTipoProduto(tipo);
        produtoB = produtoRepository.save(produtoB);

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

        entrevistador = new Entrevistador();
        entrevistador.setNome("Entrevistador Base");
        entrevistador.setEmail("entrevistador@teste.com");
        entrevistador.setSenhaHash("hash-bcrypt-que-nunca-e-validado");
        entrevistador = entrevistadorRepository.save(entrevistador);
    }

    /**
     * Monta um item de doação.
     *
     * @param produto    produto doado
     * @param quantidade quantidade
     * @return item pronto para uso
     */
    private DoacaoProdutoCreateDTO item(Produto produto, int quantidade) {
        return new DoacaoProdutoCreateDTO(
                produto.getId(), quantidade, LocalDate.of(2026, 6, 1));
    }

    @Test
    @DisplayName("cria doação com itens, recebedor e entrevistador")
    void criaDoacaoCompleta() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador Anônimo",
                recebedor.getId(), entrevistador.getId(),
                List.of(item(produtoA, 3), item(produtoB, 1))));

        assertThat(criada.id()).isNotNull();
        assertThat(criada.nomeDoador()).isEqualTo("Doador Anônimo");
        assertThat(criada.recebedor().id()).isEqualTo(recebedor.getId());
        assertThat(criada.entrevistador().email()).isEqualTo("entrevistador@teste.com");
        assertThat(criada.produtos()).hasSize(2);
    }

    @Test
    @DisplayName("resposta aninha o entrevistador sem o hash de senha")
    void respostaNaoExpoeHashDeSenha() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), null,
                null, entrevistador.getId(),
                List.of(item(produtoA, 1))));

        assertThat(criada.entrevistador().nome()).isEqualTo("Entrevistador Base");
        assertThat(Arrays.stream(EntrevistadorResponseDTO.class.getRecordComponents())
                .map(RecordComponent::getName))
                .containsExactlyInAnyOrder("id", "nome", "email");
    }

    @Test
    @DisplayName("doação sem recebedor e sem entrevistador é válida")
    void criaDoacaoSemVinculos() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador", null, null,
                List.of(item(produtoA, 1))));

        assertThat(criada.recebedor()).isNull();
        assertThat(criada.entrevistador()).isNull();
    }

    @Test
    @DisplayName("doação com lista de produtos nula fica sem itens")
    void criaDoacaoSemProdutos() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador", null, null, null));

        assertThat(criada.produtos()).isEmpty();
    }

    @Test
    @DisplayName("recusa criação com recebedor inexistente")
    void recusaCriacaoComRecebedorInexistente() {
        assertThatThrownBy(() -> doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), null, UUID.randomUUID(), null,
                List.of(item(produtoA, 1)))))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Recebedor not found: ");
    }

    @Test
    @DisplayName("recusa criação com entrevistador inexistente")
    void recusaCriacaoComEntrevistadorInexistente() {
        assertThatThrownBy(() -> doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), null, null, UUID.randomUUID(),
                List.of(item(produtoA, 1)))))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Entrevistador not found: ");
    }

    @Test
    @DisplayName("recusa criação com produto inexistente")
    void recusaCriacaoComProdutoInexistente() {
        DoacaoProdutoCreateDTO invalido = new DoacaoProdutoCreateDTO(
                UUID.randomUUID(), 1, LocalDate.of(2026, 6, 1));

        assertThatThrownBy(() -> doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), null, null, null, List.of(invalido))))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Produto not found: ");
    }

    @Test
    @DisplayName("recusa criação com o mesmo produto repetido na lista")
    void recusaProdutoDuplicadoNaCriacao() {
        DoacaoCreateDTO dto = new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), null, null, null,
                List.of(item(produtoA, 1), item(produtoA, 2)));

        assertThatThrownBy(() -> doacaoService.create(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Produto duplicado na doação: " + produtoA.getId());
    }

    @Test
    @DisplayName("busca por id devolve os itens com o produto aninhado")
    void buscaPorIdComItens() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador", null, null,
                List.of(item(produtoA, 2))));

        DoacaoResponseDTO encontrada = doacaoService.findById(criada.id());

        assertThat(encontrada.produtos()).hasSize(1);
        assertThat(encontrada.produtos().get(0).quantidade()).isEqualTo(2);
        assertThat(encontrada.produtos().get(0).produto().id()).isEqualTo(produtoA.getId());
        assertThat(encontrada.produtos().get(0).dataValidade()).isEqualTo(LocalDate.of(2026, 6, 1));
    }

    @Test
    @DisplayName("busca por id inexistente falha")
    void buscaPorIdInexistenteFalha() {
        assertThatThrownBy(() -> doacaoService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Doacao not found: ");
    }

    @Test
    @DisplayName("lista todas as doações")
    void listaTodas() {
        doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), null, null, null, List.of(item(produtoA, 1))));

        assertThat(doacaoService.findAll(PAGINA).getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("atualização substitui a lista inteira de itens")
    void atualizacaoSubstituiListaDeItens() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador", null, null,
                List.of(item(produtoA, 1), item(produtoB, 1))));

        DoacaoResponseDTO atualizada = doacaoService.update(
                criada.id(),
                new DoacaoUpdateDTO(
                        LocalDate.of(2024, 4, 2), "Outro Doador", null, null,
                        List.of(item(produtoB, 5))));

        assertThat(atualizada.nomeDoador()).isEqualTo("Outro Doador");
        assertThat(atualizada.produtos()).hasSize(1);
        assertThat(atualizada.produtos().get(0).produto().id()).isEqualTo(produtoB.getId());
        assertThat(atualizada.produtos().get(0).quantidade()).isEqualTo(5);
    }

    @Test
    @DisplayName("atualização com a mesma lista não quebra pela chave composta duplicada")
    void atualizacaoComAMesmaListaNaoQuebra() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador", null, null,
                List.of(item(produtoA, 1), item(produtoB, 2))));

        DoacaoResponseDTO atualizada = doacaoService.update(
                criada.id(),
                new DoacaoUpdateDTO(
                        LocalDate.of(2024, 3, 1), "Doador", null, null,
                        List.of(item(produtoA, 1), item(produtoB, 2))));

        assertThat(atualizada.produtos()).hasSize(2);
    }

    @Test
    @DisplayName("atualização com lista vazia remove todos os itens")
    void atualizacaoComListaVaziaRemoveTudo() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador", null, null,
                List.of(item(produtoA, 1))));

        DoacaoResponseDTO atualizada = doacaoService.update(
                criada.id(),
                new DoacaoUpdateDTO(LocalDate.of(2024, 3, 1), "Doador", null, null, List.of()));

        assertThat(atualizada.produtos()).isEmpty();
        assertThat(doacaoProdutoRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("recusa atualização com produto repetido na lista")
    void recusaProdutoDuplicadoNaAtualizacao() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador", null, null,
                List.of(item(produtoA, 1))));

        assertThatThrownBy(() -> doacaoService.update(
                criada.id(),
                new DoacaoUpdateDTO(
                        LocalDate.of(2024, 3, 1), "Doador", null, null,
                        List.of(item(produtoB, 1), item(produtoB, 2)))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Produto duplicado na doação: " + produtoB.getId());
    }

    @Test
    @DisplayName("atualização de doação inexistente falha")
    void atualizacaoDeDoacaoInexistenteFalha() {
        assertThatThrownBy(() -> doacaoService.update(
                UUID.randomUUID(),
                new DoacaoUpdateDTO(LocalDate.of(2024, 1, 1), null, null, null, List.of())))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("atualização troca o recebedor da doação")
    void atualizacaoTrocaRecebedor() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador", recebedor.getId(), null,
                List.of(item(produtoA, 1))));

        DoacaoResponseDTO atualizada = doacaoService.update(
                criada.id(),
                new DoacaoUpdateDTO(
                        LocalDate.of(2024, 3, 1), "Doador", null, null,
                        List.of(item(produtoA, 1))));

        assertThat(atualizada.recebedor()).isNull();
    }

    @Test
    @DisplayName("remove doação e os itens junto")
    void removeDoacaoEItens() {
        DoacaoResponseDTO criada = doacaoService.create(new DoacaoCreateDTO(
                LocalDate.of(2024, 3, 1), "Doador", null, null,
                List.of(item(produtoA, 1))));

        doacaoService.delete(criada.id());

        assertThat(doacaoRepository.findById(criada.id())).isEmpty();
        assertThat(doacaoProdutoRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("remoção de doação inexistente falha")
    void removeDoacaoInexistenteFalha() {
        assertThatThrownBy(() -> doacaoService.delete(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }

}
