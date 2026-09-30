package com.vonlanplace.doacao.cesta;

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
 * Renderização das páginas de cesta, no mesmo espírito dos demais
 * {@code *PaginasTest}: nada vale sem o contexto de primavera montado, então
 * o que esses casos pegam de verdade é mapeamento errado entre controller,
 * nome de view e fragmento, e o derivado de query que quebra na subida.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Renderização das páginas de cesta")
class CestaPaginasTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("a listagem de cestas renderiza")
    void listar() throws Exception {
        mockMvc.perform(get("/cesta/listar"))
                .andExpect(status().isOk())
                .andExpect(view().name("cesta/listar"))
                .andExpect(content().string(containsString("Nenhuma cesta cadastrada")));
    }

    @Test
    @DisplayName("o formulário de cadastro tem data e select de recebedor")
    void formCreate() throws Exception {
        mockMvc.perform(get("/cesta/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("cesta/form"))
                .andExpect(content().string(containsString("Data de entrega")))
                .andExpect(content().string(containsString("Recebedor")))
                .andExpect(content().string(containsString("Selecione")))
                .andExpect(content().string(containsString("Salvar")));
    }

    @Test
    @DisplayName("cesta inexistente responde 404 em HTML")
    void cestaInexistente() throws Exception {
        mockMvc.perform(get("/cesta/detalhe/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("erro/404"))
                .andExpect(content().string(not(containsString("problem+json"))));
    }
}