package com.vonlanplace.doacao.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Confere que as páginas de marca e tipo renderizam de verdade.
 *
 * <p><strong>Por que um teste de renderização e não só o build.</strong> O build
 * passa com template quebrado, com fragmento renomeado e com rota que não
 * existe. O que quebra é o render: o Thymeleaf só avalia a página quando ela é
 * pedida, e aí aparece o {@code TemplateInputException} com o nome do atributo
 * inexistente e a linha do HTML. A variável {@code page} que o
 * {@code th:each} espera, um {@code th:replace} apontando para fragmento que não
 * existe mais, e um {@code th:object} sem atributo no model só falham aqui.</p>
 *
 * <p>Não há dados de teste porque as duas telas renderizam vazio: marca e tipo
 * tem um único campo, e o estado vazio é justamente o que falha com
 * {@code #lists.isEmpty(page)} sobre uma {@code Page}.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Renderização das páginas de marca e tipo")
class MarcaTipoPaginasTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("a listagem de marca renderiza e não estoura no Page")
    void listarMarca() throws Exception {
        mockMvc.perform(get("/marca/listar").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("marca/listar"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nenhuma marca cadastrada")));
    }

    @Test
    @DisplayName("a listagem de tipo renderiza")
    void listarTipo() throws Exception {
        mockMvc.perform(get("/tipo/listar").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("tipo/listar"));
    }

    @Test
    @DisplayName("o formulário de cadastro de marca abre")
    void formMarcaCreate() throws Exception {
        mockMvc.perform(get("/marca/create").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("marca/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nome da marca")));
    }

    @Test
    @DisplayName("o formulário de cadastro de tipo abre")
    void formTipoCreate() throws Exception {
        mockMvc.perform(get("/tipo/create").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("tipo/form"));
    }

    @Test
    @DisplayName("editar marca inexistente responde 404 em HTML, não em JSON")
    void editarMarcaInexistente() throws Exception {
        mockMvc.perform(get("/marca/editar/nao-existe").session(SessaoAutenticacao.logada()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("erro/404"))
                .andExpect(content().string(
                        org.hamcrest.Matchers.not(
                                org.hamcrest.Matchers.containsString("problem+json"))));
    }

    @Test
    @DisplayName("o formulário de produto abre com os selects de marca e tipo")
    void formProduto() throws Exception {
        mockMvc.perform(get("/produto/create").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("produto/form"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Código de barras")));
    }
}
