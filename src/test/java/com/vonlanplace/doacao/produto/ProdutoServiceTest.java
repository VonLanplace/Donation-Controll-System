package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoRepository;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link ProdutoService} contra o banco H2 real.
 *
 * <p><strong>Por que {@code @SpringBootTest} e não Mockito.</strong> O que este serviço
 * faz de interessante está justamente no que não é método: o
 * {@code @EntityGraph} do repositório, os nomes derivados {@code findAllByMarcaProduto}
 * e {@code findAllByTipoProduto}, a chave composta das entidades e a resolução por nome.
 * Um mock de repositório passaria em cima de tudo isso e não encontraria erro nenhum.</p>
 *
 * <p><strong>Por que {@code @Transactional} na classe.</strong> O contexto de teste
 * enrola tudo numa transação e dá rollback no fim de cada método. As transações dos
 * serviços se juntam à do teste em vez de abrir uma nova — por isso dá para ler o
 * resultado logo depois de gravar, sem {@code flush} nem limpeza manual entre casos.</p>
 *
 * <p><strong>As fixtures são criadas por repositório, não por serviço, sempre que
 * possível.</strong> Quando o teste é do {@code ProdutoService}, montar a marca e o tipo
 * pela própria API do serviço misturaria o que está sob teste com o que é só pré
 * requisição.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("ProdutoService")
class ProdutoServiceTest {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MarcaProdutoRepository marcaProdutoRepository;

    @Autowired
    private TipoProdutoRepository tipoProdutoRepository;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private MarcaProduto marca;
    private TipoProduto tipo;
    private Produto produto;

    /**
     * Cria a marca, o tipo e o produto que servem de base para os casos que não mudam
     * o cenário.
     */
    @BeforeEach
    void setUp() {
        MarcaProduto novaMarca = new MarcaProduto();
        novaMarca.setNome("Marca Base");
        marca = marcaProdutoRepository.save(novaMarca);

        TipoProduto novoTipo = new TipoProduto();
        novoTipo.setNome("Tipo Base");
        tipo = tipoProdutoRepository.save(novoTipo);

        produto = new Produto();
        produto.setCodigoBarras("7891000100103");
        produto.setMarcaProduto(marca);
        produto.setTipoProduto(tipo);
        produto = produtoRepository.save(produto);
    }

    @Test
    @DisplayName("cria produto resolvendo marca e tipo pelo nome")
    void criaProdutoComRelacoesResolvidasPorNome() {
        MarcaProduto outraMarca = new MarcaProduto();
        outraMarca.setNome("Nike");
        marcaProdutoRepository.save(outraMarca);
        TipoProduto outroTipo = new TipoProduto();
        outroTipo.setNome("Calcado");
        tipoProdutoRepository.save(outroTipo);

        ProdutoResponseDTO criado = produtoService.create(
                new ProdutoCreateDTO("7891000100107", "Nike", "Calcado"));

        assertThat(criado.id()).isNotNull();
        assertThat(criado.codigoBarras()).isEqualTo("7891000100107");
        assertThat(criado.marcaProduto().nome()).isEqualTo("Nike");
        assertThat(criado.tipoProduto().nome()).isEqualTo("Calcado");
    }

    @Test
    @DisplayName("recusa criação quando a marca informada não existe")
    void recusaCriacaoComMarcaInexistente() {
        assertThatThrownBy(() -> produtoService.create(
                new ProdutoCreateDTO("7891000100107", "Marca Fantasma", "Tipo Base")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Marca Produto not found: Marca Fantasma");
    }

    @Test
    @DisplayName("recusa criação quando o tipo informado não existe")
    void recusaCriacaoComTipoInexistente() {
        assertThatThrownBy(() -> produtoService.create(
                new ProdutoCreateDTO("7891000100107", "Marca Base", "Tipo Fantasma")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Tipo Produto not found: Tipo Fantasma");
    }

    @Test
    @DisplayName("busca por id devolve marca e tipo aninhados sem id")
    void buscaPorIdAninhaRelacoesSemId() {
        ProdutoResponseDTO encontrado = produtoService.findById(produto.getId());

        assertThat(encontrado.codigoBarras()).isEqualTo("7891000100103");
        assertThat(encontrado.marcaProduto().nome()).isEqualTo("Marca Base");
        assertThat(encontrado.tipoProduto().nome()).isEqualTo("Tipo Base");
    }

    @Test
    @DisplayName("busca por id inexistente devolve 404 em forma de EntityNotFoundException")
    void buscaPorIdInexistenteFalha() {
        UUID desconhecido = UUID.randomUUID();

        assertThatThrownBy(() -> produtoService.findById(desconhecido))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Produto not found");
    }

    @Test
    @DisplayName("atualiza produto trocando marca e tipo pelos novos nomes")
    void atualizaProdutoTrocandoRelacoes() {
        MarcaProduto novaMarca = new MarcaProduto();
        novaMarca.setNome("Reebok");
        marcaProdutoRepository.save(novaMarca);

        ProdutoResponseDTO atualizado = produtoService.update(
                produto.getId(),
                new ProdutoUpdateDTO("7891000100109", "Reebok", "Tipo Base"));

        assertThat(atualizado.codigoBarras()).isEqualTo("7891000100109");
        assertThat(atualizado.marcaProduto().nome()).isEqualTo("Reebok");
    }

    @Test
    @DisplayName("atualização não apaga marca nem tipo quando o mapper os ignora")
    void atualizacaoNaoApagaRelacoesResolvidas() {
        produtoService.update(
                produto.getId(),
                new ProdutoUpdateDTO(null, "Marca Base", "Tipo Base"));

        ProdutoResponseDTO relido = produtoService.findById(produto.getId());

        assertThat(relido.codigoBarras()).isEqualTo("7891000100103");
        assertThat(relido.marcaProduto().nome()).isEqualTo("Marca Base");
        assertThat(relido.tipoProduto().nome()).isEqualTo("Tipo Base");
    }

    @Test
    @DisplayName("atualização de produto inexistente falha")
    void atualizaProdutoInexistenteFalha() {
        UUID desconhecido = UUID.randomUUID();

        assertThatThrownBy(() -> produtoService.update(
                desconhecido, new ProdutoUpdateDTO(null, "Marca Base", "Tipo Base")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("busca por código de barras filtra exatamente o produto")
    void buscaPorCodigoDeBarras() {
        Page<ProdutoResponseDTO> pagina = produtoService.findByCodigoBarras("7891000100103", PAGINA);

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).id()).isEqualTo(produto.getId());
    }

    @Test
    @DisplayName("busca por marca usa o nome e devolve só os produtos daquela marca")
    void buscaPorMarcaPeloNome() {
        MarcaProduto outraMarca = new MarcaProduto();
        outraMarca.setNome("Adidas");
        outraMarca = marcaProdutoRepository.save(outraMarca);

        Produto adidas = new Produto();
        adidas.setCodigoBarras("7891000100107");
        adidas.setMarcaProduto(outraMarca);
        adidas.setTipoProduto(tipo);
        produtoRepository.save(adidas);

        Page<ProdutoResponseDTO> pagina = produtoService.findByMarcaProduto("Adidas", PAGINA);

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).marcaProduto().nome()).isEqualTo("Adidas");
    }

    @Test
    @DisplayName("busca por marca inexistente falha em vez de devolver página vazia")
    void buscaPorMarcaInexistenteFalha() {
        assertThatThrownBy(() -> produtoService.findByMarcaProduto("Marca Fantasma", PAGINA))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("busca por tipo usa o nome e devolve só os produtos daquele tipo")
    void buscaPorTipoPeloNome() {
        TipoProduto outroTipo = new TipoProduto();
        outroTipo.setNome("Roupa");
        outroTipo = tipoProdutoRepository.save(outroTipo);

        Produto roupa = new Produto();
        roupa.setCodigoBarras("7891000100108");
        roupa.setMarcaProduto(marca);
        roupa.setTipoProduto(outroTipo);
        produtoRepository.save(roupa);

        Page<ProdutoResponseDTO> pagina = produtoService.findByTipoProduto("Roupa", PAGINA);

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).tipoProduto().nome()).isEqualTo("Roupa");
    }

    @Test
    @DisplayName("lista todos os produtos com as relações aninhadas")
    void listaTodosComRelacoesAninhadas() {
        Page<ProdutoResponseDTO> pagina = produtoService.findAll(PAGINA);

        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().get(0).marcaProduto()).isNotNull();
        assertThat(pagina.getContent().get(0).tipoProduto()).isNotNull();
    }

    @Test
    @DisplayName("remove produto existente")
    void removeProdutoExistente() {
        produtoService.delete(produto.getId());

        assertThat(produtoRepository.findById(produto.getId())).isEmpty();
    }

    @Test
    @DisplayName("remoção de produto inexistente falha")
    void removeProdutoInexistenteFalha() {
        UUID desconhecido = UUID.randomUUID();

        assertThatThrownBy(() -> produtoService.delete(desconhecido))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("a cesta da entidade é sempre nula, por não ter caminho de escrita")
    void cestaDaEntidadePermaneceNula() {
        produtoService.findById(produto.getId());

        assertThat(produtoRepository.findById(produto.getId()))
                .isPresent()
                .get()
                .extracting(Produto::getCesta)
                .isNull();
    }
}
