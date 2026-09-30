package com.vonlanplace.doacao.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Confere que as páginas de recebedor renderizam, incluindo os dois campos
 * novos da entrevista.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Renderização das páginas de recebedor")
class RecebedorPaginasTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("a listagem de recebedor renderiza")
    void listarRecebedores() throws Exception {
        mockMvc.perform(get("/recebedor/listar"))
                .andExpect(status().isOk())
                .andExpect(view().name("recebedor/listar"))
                .andExpect(content().string(containsString("Nenhum recebedor cadastrado")));
    }

    @Test
    @DisplayName("o formulário de cadastro expõe os dois campos novos da entrevista")
    void formHasNovosCampos() throws Exception {
        mockMvc.perform(get("/recebedor/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("recebedor/form"))
                .andExpect(content().string(containsString("Recebeu cesta")))
                .andExpect(content().string(containsString("Parecer do entrevistador")))
                .andExpect(content().string(containsString("Comentários da entrevista")));
    }

    @Test
    @DisplayName("o formulário de cadastro traz o select de residências")
    void formHasResidencias() throws Exception {
        mockMvc.perform(get("/recebedor/create"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Residência")));
    }

    @Test
    @DisplayName("recebedor inexistente responde 404 em HTML")
    void recebedorInexistente() throws Exception {
        mockMvc.perform(get("/recebedor/detalhe/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("erro/404"))
                .andExpect(content().string(not(containsString("problem+json"))));
    }
}