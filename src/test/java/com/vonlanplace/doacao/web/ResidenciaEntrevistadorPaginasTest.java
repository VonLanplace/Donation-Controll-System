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
 * Confere que as páginas de residência e entrevistador renderizam.
 *
 * <p><strong>O motivo de teste de renderização estar no plano.</strong>residência
 * tem dez campos e dois selects de enum, e entrevistador tem um input de senha
 * com atributo condicional. Nada disso é conferido pelo compilador: um
 * {@code th:field} com nome errado, um fragmento de select com
 * {@code opcoes} fora do model e um {@code th:required} com expression inválida
 * só falham quando a página é pedida.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Renderização das páginas de residência e entrevistador")
class ResidenciaEntrevistadorPaginasTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("a listagem de residência renderiza")
    void listarResidencia() throws Exception {
        mockMvc.perform(get("/residencia/listar").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("residencia/listar"))
                .andExpect(content().string(containsString("Nenhuma residência cadastrada")));
    }

    @Test
    @DisplayName("o formulário de residência mostra os dois selects de enum")
    void formResidencia() throws Exception {
        mockMvc.perform(get("/residencia/create").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("residencia/form"))
                .andExpect(content().string(containsString("Tipo de ocupação")))
                .andExpect(content().string(containsString("Tipo de residência")))
                .andExpect(content().string(containsString("Logradouro")));
    }

    @Test
    @DisplayName("os enums de residência viram opções com a descrição em português")
    void enumsViramOpcoes() throws Exception {
        mockMvc.perform(get("/residencia/create").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                // name() é o valor gravado; a descrição é o texto exibido.
                // TipoOcupacao grava PROPRIEDADE e mostra "Propriedade", e
                // TipoResidencia tem um nome sem acento que só a descrição
                // deixa legível.
                .andExpect(content().string(containsString("value=\"PROPRIEDADE\"")))
                .andExpect(content().string(containsString("Bioconstrução (Taipa/Adobe)")));
    }

    @Test
    @DisplayName("a listagem de entrevistador renderiza")
    void listarEntrevistador() throws Exception {
        mockMvc.perform(get("/entrevistador/listar").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("entrevistador/listar"))
                .andExpect(content().string(containsString("Nenhum entrevistador cadastrado")));
    }

    @Test
    @DisplayName("o formulário de cadastro de entrevistador marca a senha como obrigatória")
    void formEntrevistadorCreate() throws Exception {
        mockMvc.perform(get("/entrevistador/create").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("entrevistador/form"))
                .andExpect(content().string(containsString("type=\"password\"")));
    }

    @Test
    @DisplayName("resolução de entrevistador inexistente responde 404 HTML")
    void entrevistadorInexistente() throws Exception {
        mockMvc.perform(get("/entrevistador/detalhe/00000000-0000-0000-0000-000000000000").session(SessaoAutenticacao.logada()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("erro/404"))
                .andExpect(content().string(not(containsString("problem+json"))));
    }

    @Test
    @DisplayName("resolução de residência inexistente responde 404 HTML")
    void residenciaInexistente() throws Exception {
        mockMvc.perform(get("/residencia/detalhe/00000000-0000-0000-0000-000000000000").session(SessaoAutenticacao.logada()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("erro/404"));
    }
}
