package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.produto.ProdutoCreateDTO;
import com.vonlanplace.doacao.produto.ProdutoService;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoCreateDTO;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoService;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoCreateDTO;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoService;
import com.vonlanplace.doacao.recebedor.RecebedorCreateDTO;
import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaCreateDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaService;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Renderização e fluxo das páginas de doação, incluindo o passo a passo de
 * montagem de itens que só existe aqui: o POST intermediário "Adicionar
 * produto" re-renderiza o formulário com a linha nova incorporada, e o POST de
 * "Salvar" dispara o serviço.
 *
 * <p><strong>O navio do rascunho é a sessão.</strong> {@code RascunhoDoacao} é
 * um bean de escopo de sessão, e o MockMvc carrega a sessão entre requisições
 * do mesmo teste: os {@code post("/doacao/form")} seguidos precisam achar, na
 * sessão, o rascunho que o POST anterior deixou. É isso que estes testes
 * verificam de verdade.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Páginas e fluxo de doação")
class DoacaoPaginasTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecebedorService recebedorService;

    @Autowired
    private ResidenciaService residenciaService;

    @Autowired
    private MarcaProdutoService marcaProdutoService;

    @Autowired
    private TipoProdutoService tipoProdutoService;

    @Autowired
    private ProdutoService produtoService;

    private UUID produtoId;

    private UUID recebedorId;

    @BeforeEach
    void preparar() {
        marcaProdutoService.create(new MarcaProdutoCreateDTO("MarcaTeste"));
        tipoProdutoService.create(new TipoProdutoCreateDTO("TipoTeste"));
        produtoId = produtoService.create(new ProdutoCreateDTO(
                "7891000100103", "MarcaTeste", "TipoTeste"
        )).id();
        UUID residenciaId = residenciaService.create(new ResidenciaCreateDTO(
                "Rua das Flores", "100", "01001000", "", "São Paulo",
                "Centro", 4, 2, TipoOcupacao.PROPRIEDADE, TipoResidencia.ALVENARIA_TRADICIONAL
        )).id();
        recebedorId = recebedorService.create(new RecebedorCreateDTO(
                "Cria doador", "00000000011", null, null,
                LocalDate.of(1980, 1, 1), null, null,
                true, null, null, false, false, false, false, null, residenciaId
        )).id();
    }

    @Test
    @DisplayName("a listagem de doações renderiza")
    void listar() throws Exception {
        mockMvc.perform(get("/doacao/listar"))
                .andExpect(status().isOk())
                .andExpect(view().name("doacao/listar"))
                .andExpect(content().string(containsString("Nenhuma doação cadastrada")));
    }

    @Test
    @DisplayName("o formulário novo começa sem itens e com os dois botões")
    void formNovo() throws Exception {
        mockMvc.perform(get("/doacao/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("doacao/form"))
                .andExpect(content().string(containsString("Nenhum item adicionado ainda.")))
                .andExpect(content().string(containsString("Adicionar produto")))
                .andExpect(content().string(containsString("Salvar doação")));
    }

    @Test
    @DisplayName("Adicionar produto acumula a linha no rascunho de sessão")
    void adicionarLinhaAcumula() throws Exception {
        MockHttpSession sessao = new MockHttpSession();
        mockMvc.perform(post("/doacao/form")
                        .session(sessao)
                        .param("acao", "adicionar")
                        .param("data", "2026-03-10")
                        .param("nomeDoador", "Doador Anônimo")
                        .param("recebedorId", recebedorId.toString())
                        .param("linhaProdutoId", produtoId.toString())
                        .param("linhaQuantidade", "2")
                        .param("linhaDataValidade", "2026-06-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("doacao/form"))
                .andExpect(content().string(containsString("7891000100103 · MarcaTeste")))
                .andExpect(content().string(containsString("01/06/2026")));

        mockMvc.perform(post("/doacao/form")
                        .session(sessao)
                        .param("acao", "adicionar")
                        .param("data", "2026-03-10")
                        .param("linhaProdutoId", produtoId.toString())
                        .param("linhaQuantidade", "1")
                        .param("linhaDataValidade", "2026-06-01"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Este produto já está na doação.")));
    }

    @Test
    @DisplayName("Remover linha tira a linha do rascunho")
    void removerLinha() throws Exception {
        MockHttpSession sessao = new MockHttpSession();
        mockMvc.perform(post("/doacao/form")
                        .session(sessao)
                        .param("acao", "adicionar")
                        .param("data", "2026-03-10")
                        .param("linhaProdutoId", produtoId.toString())
                        .param("linhaQuantidade", "2")
                        .param("linhaDataValidade", "2026-06-01"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/doacao/linha/0/remover").session(sessao))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nenhum item adicionado ainda.")));
    }

    @Test
    @DisplayName("validação rejeita linha com quantidade zero")
    void linhaInvalida() throws Exception {
        mockMvc.perform(post("/doacao/form")
                        .param("acao", "adicionar")
                        .param("linhaProdutoId", produtoId.toString())
                        .param("linhaQuantidade", "0")
                        .param("linhaDataValidade", "2026-06-01"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Informe o produto, uma quantidade acima de zero e a data de validade.")));
    }

    @Test
    @DisplayName("Salvar sem data devolve o formulário com o erro de data")
    void salvarSemData() throws Exception {
        mockMvc.perform(post("/doacao/form")
                        .param("acao", "salvar")
                        .param("linhaProdutoId", produtoId.toString())
                        .param("linhaQuantidade", "1")
                        .param("linhaDataValidade", "2026-06-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("doacao/form"))
                .andExpect(content().string(containsString("A data da doação é obrigatória.")));
    }

    @Test
    @DisplayName("o fluxo inteiro salva e redireciona para a listagem")
    void fluxoCompleto() throws Exception {
        MockHttpSession sessao = new MockHttpSession();
        mockMvc.perform(post("/doacao/form")
                        .session(sessao)
                        .param("acao", "adicionar")
                        .param("data", "2026-03-10")
                        .param("nomeDoador", "Doador Anônimo")
                        .param("recebedorId", recebedorId.toString())
                        .param("linhaProdutoId", produtoId.toString())
                        .param("linhaQuantidade", "3")
                        .param("linhaDataValidade", "2026-07-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("doacao/form"));

        mockMvc.perform(post("/doacao/form")
                        .session(sessao)
                        .param("acao", "salvar")
                        .param("data", "2026-03-10")
                        .param("nomeDoador", "Doador Anônimo")
                        .param("recebedorId", recebedorId.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doacao/listar"));
    }
}