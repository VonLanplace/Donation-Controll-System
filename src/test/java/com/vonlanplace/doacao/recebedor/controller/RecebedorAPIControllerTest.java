package com.vonlanplace.doacao.recebedor.controller;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import com.vonlanplace.doacao.support.annotations.WebIntegracaoTest;
import com.vonlanplace.doacao.support.fixtures.RecebedorFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API REST de recebedores.
 */
@WebIntegracaoTest
@DisplayName("RecebedorAPIController")
class RecebedorAPIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    private Residencia residencia;

    @BeforeEach
    void setUp() {
        residencia = residenciaRepository.save(RecebedorFixtures.residencia());
    }

    @Test
    @DisplayName("lista recebedores paginados")
    void listaRecebedores() throws Exception {
        recebedorRepository.save(recebedor("Ana Souza", "11122233344", "ana@pix.com"));

        mockMvc.perform(get("/api/recebedor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nomeCompleto").value("Ana Souza"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("cria recebedor com 201")
    void criaRecebedor() throws Exception {
        mockMvc.perform(post("/api/recebedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Ana Souza", "11122233344", "ana@pix.com")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.cpf").value("11122233344"))
                .andExpect(jsonPath("$.residencia.id").value(residencia.getId().toString()));
    }

    @Test
    @DisplayName("corpo inválido responde 400 problem+json")
    void corpoInvalido() throws Exception {
        mockMvc.perform(post("/api/recebedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cpf":"11122233344","dataNascimento":"1990-01-01",
                                 "residenciaId":"%s"}
                                """.formatted(residencia.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("CPF duplicado responde 409")
    void cpfDuplicado() throws Exception {
        recebedorRepository.save(recebedor("Ana Souza", "11122233344", "ana@pix.com"));

        mockMvc.perform(post("/api/recebedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Outra", "11122233344", "outra@pix.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de Estado"))
                .andExpect(jsonPath("$.detail").value("CPF já cadastrado para outro recebedor"));
    }

    @Test
    @DisplayName("busca por id e devolve 404 quando não existe")
    void buscaPorId() throws Exception {
        Recebedor recebedor = recebedorRepository.save(
                recebedor("Ana Souza", "11122233344", "ana@pix.com"));

        mockMvc.perform(get("/api/recebedor/{id}", recebedor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto").value("Ana Souza"));

        mockMvc.perform(get("/api/recebedor/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso Não Encontrado"));
    }

    @Test
    @DisplayName("atualiza recebedor e recusa corpo inválido")
    void atualizaRecebedor() throws Exception {
        Recebedor recebedor = recebedorRepository.save(
                recebedor("Ana Souza", "11122233344", "ana@pix.com"));

        mockMvc.perform(put("/api/recebedor/{id}", recebedor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Ana Editada", "11122233344", "ana@pix.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeCompleto").value("Ana Editada"));

        mockMvc.perform(put("/api/recebedor/{id}", recebedor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpf\":\"11122233344\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("apaga recebedor e devolve 404 na sequência")
    void apagaRecebedor() throws Exception {
        Recebedor recebedor = recebedorRepository.save(
                recebedor("Ana Souza", "11122233344", "ana@pix.com"));

        mockMvc.perform(delete("/api/recebedor/{id}", recebedor.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/recebedor/{id}", recebedor.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("busca por nome, data, residência, CPF e PIX")
    void buscaPorFiltros() throws Exception {
        Recebedor ana = recebedorRepository.save(
                recebedor("Ana Souza", "11122233344", "ana@pix.com"));
        Recebedor bia = recebedor("Bia Lima", "55566677788", "bia@pix.com");
        bia.setDataNascimento(LocalDate.of(1991, 1, 1));
        recebedorRepository.save(bia);

        mockMvc.perform(get("/api/recebedor/search").param("nomeCompleto", "Ana Souza"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
        mockMvc.perform(get("/api/recebedor/search")
                        .param("dataNascimento", ana.getDataNascimento().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
        mockMvc.perform(get("/api/recebedor/search")
                        .param("residenciaId", residencia.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
        mockMvc.perform(get("/api/recebedor/search").param("cpf", "11122233344"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
        mockMvc.perform(get("/api/recebedor/search").param("pix", "bia@pix.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("filtros são avaliados na ordem nome, data, residência, CPF e PIX")
    void precedenciaDosFiltros() throws Exception {
        recebedorRepository.save(recebedor("Ana Souza", "11122233344", "ana@pix.com"));
        recebedorRepository.save(recebedor("Bia Lima", "55566677788", "bia@pix.com"));

        mockMvc.perform(get("/api/recebedor/search")
                        .param("nomeCompleto", "Ana Souza")
                        .param("cpf", "55566677788"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nomeCompleto").value("Ana Souza"));
    }

    @Test
    @DisplayName("sem filtro lista todos")
    void semFiltroListaTodos() throws Exception {
        recebedorRepository.save(recebedor("Ana Souza", "11122233344", "ana@pix.com"));
        recebedorRepository.save(recebedor("Bia Lima", "55566677788", "bia@pix.com"));

        mockMvc.perform(get("/api/recebedor/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("CPF inexistente na busca responde 404")
    void buscaCpfInexistente() throws Exception {
        mockMvc.perform(get("/api/recebedor/search").param("cpf", "00000000000"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Recebedor not found by cpf")));
    }

    private Recebedor recebedor(String nome, String cpf, String pix) {
        Recebedor recebedor = RecebedorFixtures.recebedor(residencia);
        recebedor.setNomeCompleto(nome);
        recebedor.setCpf(cpf);
        recebedor.setPix(pix);
        recebedor.setDataNascimento(LocalDate.of(1990, 3, 10));
        return recebedor;
    }

    private String json(String nome, String cpf, String pix) {
        return """
                {
                  "nomeCompleto": "%s",
                  "cpf": "%s",
                  "rgNum": "12345",
                  "rgEmissor": "SSP",
                  "dataNascimento": "1990-03-10",
                  "telefonePessoal": "81999999999",
                  "leEscreve": true,
                  "email": "pessoa@email.com",
                  "pix": "%s",
                  "necessitaJuridico": false,
                  "necessitaPsicologo": false,
                  "parecerEntrevistador": false,
                  "comentarios": "observação",
                  "residenciaId": "%s"
                }
                """.formatted(nome, cpf, pix, residencia.getId());
    }
}
