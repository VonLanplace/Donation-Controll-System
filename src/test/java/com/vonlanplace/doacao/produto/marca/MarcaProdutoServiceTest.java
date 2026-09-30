package com.vonlanplace.doacao.produto.marca;

import com.vonlanplace.doacao.produto.Produto;
import com.vonlanplace.doacao.produto.ProdutoRepository;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoRepository;
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
 * Testes de integração do {@link MarcaProdutoService} contra o banco H2 real.
 *
 * <p><strong>O que estes testes defendem é o contrato de identificação por nome.</strong>
 * Marca não é localizada por id em nenhum ponto da API, então a cobertura que importa é:
 * criação bloqueada por nome repetido, renomeação que não colide consigo mesma, e a
 * trava de exclusão que depende do {@code existsByMarcaProduto} do
 * {@code ProdutoRepository}.</p>
 *
 * <p><strong>A trava de exclusão é o ponto que mais merece teste.</strong> O método
 * consulta o repositório de produtos e só então remove. Um mock aqui daria o resultado
 * certo sem exercitar o nome derivado da query, que é a parte que costuma quebrar.</p>
 *
 * <p>Cada método roda em transação própria do framework de teste, com rollback no fim,
 * então os casos não enxergam o estado uns dos outros.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("MarcaProdutoService")
class MarcaProdutoServiceTest {

    @Autowired
    private MarcaProdutoService marcaProdutoService;

    @Autowired
    private MarcaProdutoRepository marcaProdutoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private TipoProdutoRepository tipoProdutoRepository;

    private static final PageRequest PAGINA = PageRequest.of(0, 20);

    private MarcaProduto marca;

    /**
     * Cria a marca usada como base.
     */
    @BeforeEach
    void setUp() {
        MarcaProduto novaMarca = new MarcaProduto();
        novaMarca.setNome("Nike");
        marca = marcaProdutoRepository.save(novaMarca);
    }

    @Test
    @DisplayName("cria marca e devolve só o nome")
    void criaMarca() {
        MarcaProdutoResponseDTO criada = marcaProdutoService.create(
                new MarcaProdutoCreateDTO("Adidas"));

        assertThat(criada.nome()).isEqualTo("Adidas");
        assertThat(marcaProdutoRepository.findByNome("Adidas")).isPresent();
    }

    @Test
    @DisplayName("recusa criação com nome já cadastrado")
    void recusaNomeDuplicadoNaCriacao() {
        assertThatThrownBy(() -> marcaProdutoService.create(new MarcaProdutoCreateDTO("Nike")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nome já em uso por outra marca");
    }

    @Test
    @DisplayName("busca por nome devolve a marca")
    void buscaPorNome() {
        MarcaProdutoResponseDTO encontrada = marcaProdutoService.findByNome("Nike");

        assertThat(encontrada.nome()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("busca por nome inexistente falha")
    void buscaPorNomeInexistenteFalha() {
        assertThatThrownBy(() -> marcaProdutoService.findByNome("Marca Fantasma"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Marca Produto not found");
    }

    @Test
    @DisplayName("lista as marcas paginadas")
    void listaMarcas() {
        marcaProdutoService.create(new MarcaProdutoCreateDTO("Adidas"));

        assertThat(marcaProdutoService.findAll(PAGINA).getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("renomeia marca existente")
    void renomeiaMarca() {
        MarcaProdutoResponseDTO atualizada = marcaProdutoService.update(
                "Nike", new MarcaProdutoUpdateDTO("Nike Brasil"));

        assertThat(atualizada.nome()).isEqualTo("Nike Brasil");
        assertThat(marcaProdutoRepository.findByNome("Nike")).isEmpty();
    }

    @Test
    @DisplayName("renomear para o mesmo nome não entra em conflito consigo mesma")
    void renomearParaOMesmoNomeNaoConflita() {
        MarcaProdutoResponseDTO atualizada = marcaProdutoService.update(
                "Nike", new MarcaProdutoUpdateDTO("Nike"));

        assertThat(atualizada.nome()).isEqualTo("Nike");
    }

    @Test
    @DisplayName("renomear para nome já usado por outra marca é recusado")
    void renomearParaNomeOcupadoFalha() {
        marcaProdutoService.create(new MarcaProdutoCreateDTO("Adidas"));

        assertThatThrownBy(() -> marcaProdutoService.update(
                "Nike", new MarcaProdutoUpdateDTO("Adidas")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Nome já em uso por outra marca");
    }

    @Test
    @DisplayName("renomear marca inexistente falha")
    void renomearMarcaInexistenteFalha() {
        assertThatThrownBy(() -> marcaProdutoService.update(
                "Marca Fantasma", new MarcaProdutoUpdateDTO("Novo Nome")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("remove marca sem produtos vinculados")
    void removeMarcaSemProdutos() {
        marcaProdutoService.delete("Nike");

        assertThat(marcaProdutoRepository.findByNome("Nike")).isEmpty();
    }

    @Test
    @DisplayName("recusa remover marca que tem produto vinculado")
    void recusaRemoverMarcaComProduto() {
        TipoProduto tipo = new TipoProduto();
        tipo.setNome("Calcado");
        tipo = tipoProdutoRepository.save(tipo);

        Produto produto = new Produto();
        produto.setCodigoBarras("7891000100103");
        produto.setMarcaProduto(marca);
        produto.setTipoProduto(tipo);
        produtoRepository.save(produto);

        assertThatThrownBy(() -> marcaProdutoService.delete("Nike"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Não é possível excluir marca com produtos vinculados");
        assertThat(marcaProdutoRepository.findByNome("Nike")).isPresent();
    }

    @Test
    @DisplayName("remover marca inexistente falha")
    void removeMarcaInexistenteFalha() {
        assertThatThrownBy(() -> marcaProdutoService.delete("Marca Fantasma"))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
