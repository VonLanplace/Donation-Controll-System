package com.vonlanplace.doacao.produto.tipo;

import com.vonlanplace.doacao.produto.Produto;
import com.vonlanplace.doacao.produto.ProdutoRepository;
import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do {@link TipoProdutoService} contra o banco H2 real.
 *
 * <p><strong>Espelha o {@code MarcaProdutoServiceTest} caso a caso.</strong> Os dois
 * serviços têm o mesmo desenho, de modo que os mesmos nomes de teste cobrem as duas
 * implementações. Se algum dos dois divergir no futuro, esta suíte é o que denuncia.</p>
 *
 * <p>A trava de exclusão exercita o {@code existsByTipoProduto} do
 * {@code ProdutoRepository}, que é query derivada e por isso precisa de banco real para
 * ser verificada de verdade.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("TipoProdutoService")
class TipoProdutoServiceTest {

    @Autowired
    private TipoProdutoService tipoProdutoService;

    @Autowired
    private TipoProdutoRepository tipoProdutoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MarcaProdutoRepository marcaProdutoRepository;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private TipoProduto tipo;

    /**
     * Cria o tipo usado como base.
     */
    @BeforeEach
    void setUp() {
        TipoProduto novoTipo = new TipoProduto();
        novoTipo.setNome("Calcado");
        tipo = tipoProdutoRepository.save(novoTipo);
    }

    @Test
    @DisplayName("cria tipo e devolve só o nome")
    void criaTipo() {
        TipoProdutoResponseDTO criado = tipoProdutoService.create(
                new TipoProdutoCreateDTO("Roupa"));

        assertThat(criado.nome()).isEqualTo("Roupa");
        assertThat(tipoProdutoRepository.findByNome("Roupa")).isPresent();
    }

    @Test
    @DisplayName("recusa criação com nome já cadastrado")
    void recusaNomeDuplicadoNaCriacao() {
        assertThatThrownBy(() -> tipoProdutoService.create(new TipoProdutoCreateDTO("Calcado")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nome já em uso por outro tipo de produto");
    }

    @Test
    @DisplayName("busca por nome devolve o tipo")
    void buscaPorNome() {
        assertThat(tipoProdutoService.findByNome("Calcado").nome()).isEqualTo("Calcado");
    }

    @Test
    @DisplayName("busca por nome inexistente falha")
    void buscaPorNomeInexistenteFalha() {
        assertThatThrownBy(() -> tipoProdutoService.findByNome("Tipo Fantasma"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Tipo Produto not found");
    }

    @Test
    @DisplayName("lista os tipos paginados")
    void listaTipos() {
        tipoProdutoService.create(new TipoProdutoCreateDTO("Roupa"));

        assertThat(tipoProdutoService.findAll(PAGINA).getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("renomeia tipo existente")
    void renomeiaTipo() {
        assertThat(tipoProdutoService.update("Calcado", new TipoProdutoUpdateDTO("Calcados")).nome())
                .isEqualTo("Calcados");
        assertThat(tipoProdutoRepository.findByNome("Calcado")).isEmpty();
    }

    @Test
    @DisplayName("renomear para o mesmo nome não entra em conflito consigo mesmo")
    void renomearParaOMesmoNomeNaoConflita() {
        assertThat(tipoProdutoService.update("Calcado", new TipoProdutoUpdateDTO("Calcado")).nome())
                .isEqualTo("Calcado");
    }

    @Test
    @DisplayName("renomear para nome já usado por outro tipo é recusado")
    void renomearParaNomeOcupadoFalha() {
        tipoProdutoService.create(new TipoProdutoCreateDTO("Roupa"));

        assertThatThrownBy(() -> tipoProdutoService.update(
                "Calcado", new TipoProdutoUpdateDTO("Roupa")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nome já em uso por outro tipo de produto");
    }

    @Test
    @DisplayName("renomear tipo inexistente falha")
    void renomearTipoInexistenteFalha() {
        assertThatThrownBy(() -> tipoProdutoService.update(
                "Tipo Fantasma", new TipoProdutoUpdateDTO("Novo Nome")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("remove tipo sem produtos vinculados")
    void removeTipoSemProdutos() {
        tipoProdutoService.delete("Calcado");

        assertThat(tipoProdutoRepository.findByNome("Calcado")).isEmpty();
    }

    @Test
    @DisplayName("recusa remover tipo que tem produto vinculado")
    void recusaRemoverTipoComProduto() {
        MarcaProduto marca = new MarcaProduto();
        marca.setNome("Nike");
        marca = marcaProdutoRepository.save(marca);

        Produto produto = new Produto();
        produto.setCodigoBarras("7891000100103");
        produto.setMarcaProduto(marca);
        produto.setTipoProduto(tipo);
        produtoRepository.save(produto);

        assertThatThrownBy(() -> tipoProdutoService.delete("Calcado"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Não é possível excluir tipo com produtos vinculados");
        assertThat(tipoProdutoRepository.findByNome("Calcado")).isPresent();
    }

    @Test
    @DisplayName("remover tipo inexistente falha")
    void removeTipoInexistenteFalha() {
        assertThatThrownBy(() -> tipoProdutoService.delete("Tipo Fantasma"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
