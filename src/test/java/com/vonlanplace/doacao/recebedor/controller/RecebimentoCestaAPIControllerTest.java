package com.vonlanplace.doacao.recebedor.controller;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCesta;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaRepository;
import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import com.vonlanplace.doacao.support.annotations.WebIntegracaoTest;
import com.vonlanplace.doacao.support.config.RelogioFixoTestConfig;
import com.vonlanplace.doacao.support.fixtures.RecebedorFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API REST do histórico de cestas.
 */
@WebIntegracaoTest
@Import(RelogioFixoTestConfig.class)
@DisplayName("RecebimentoCestaAPIController")
class RecebimentoCestaAPIControllerTest {

    private static final YearMonth MES_ATUAL = YearMonth.of(2026, 1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private RecebimentoCestaRepository recebimentoCestaRepository;

    private Residencia residencia;
    private Recebedor recebedor;

    @BeforeEach
    void setUp() {
        residencia = residenciaRepository.save(RecebedorFixtures.residencia());
        recebedor = recebedorRepository.save(RecebedorFixtures.recebedor(residencia));
    }

    @Test
    @DisplayName("elegibilidade por documento usa o mês do relógio")
    void elegibilidadePorDocumento() throws Exception {
        mockMvc.perform(get("/api/recebimento-cesta/elegibilidade")
                        .param("documento", recebedor.getCpf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.elegivel").value(true))
                .andExpect(jsonPath("$.dataReferencia").value("2026-01-01"))
                .andExpect(jsonPath("$.motivo").value("Elegível para receber a cesta"));
    }

    @Test
    @DisplayName("elegibilidade por id aceita ano e mês explícitos")
    void elegibilidadePorId() throws Exception {
        mockMvc.perform(get("/api/recebimento-cesta/elegibilidade/{id}", recebedor.getId())
                        .param("ano", "2026")
                        .param("mes", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recebedorId").value(recebedor.getId().toString()))
                .andExpect(jsonPath("$.dataReferencia").value("2026-01-01"));
    }

    @Test
    @DisplayName("documento inexistente responde 400")
    void documentoInexistente() throws Exception {
        mockMvc.perform(get("/api/recebimento-cesta/elegibilidade")
                        .param("documento", "00000000000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Requisição Inválida"))
                .andExpect(jsonPath("$.detail").value(
                        "Nenhum recebedor encontrado para o documento informado"));
    }

    @Test
    @DisplayName("documento com mais de um recebedor responde 400")
    void documentoDuplicado() throws Exception {
        Recebedor outro = RecebedorFixtures.recebedor(residencia);
        outro.setRgNum("12345");
        recebedor.setRgNum("12345");
        recebedorRepository.save(recebedor);
        recebedorRepository.save(outro);

        mockMvc.perform(get("/api/recebimento-cesta/elegibilidade")
                        .param("documento", "12345"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(
                        "Mais de um recebedor encontrado para o documento informado"));
    }

    @Test
    @DisplayName("elegibilidade de recebedor inexistente responde 404")
    void elegibilidadeInexistente() throws Exception {
        mockMvc.perform(get("/api/recebimento-cesta/elegibilidade/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("histórico vazio e de recebedor inexistente")
    void historico() throws Exception {
        mockMvc.perform(get("/api/recebimento-cesta/historico/{id}", recebedor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/recebimento-cesta/historico/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("registra cesta e repete o POST sem duplicar")
    void registraCestaIdempotente() throws Exception {
        mockMvc.perform(post("/api/recebimento-cesta/{id}", recebedor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cestaJson("primeira")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RECEBEU"))
                .andExpect(jsonPath("$.dataEntrega").value("2026-01-15"));

        mockMvc.perform(post("/api/recebimento-cesta/{id}", recebedor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cestaJson("editada")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/recebimento-cesta/historico/{id}", recebedor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].observacao").value("editada"));
    }

    @Test
    @DisplayName("mês futuro responde 409")
    void mesFuturo() throws Exception {
        mockMvc.perform(post("/api/recebimento-cesta/{id}", recebedor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ano":2026,"mes":"FEVEREIRO","status":"RECEBEU"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de Estado"))
                .andExpect(jsonPath("$.detail").value("Mês de referência não pode ser futuro"));
    }

    @Test
    @DisplayName("quinto mês consecutivo responde 409")
    void limiteConsecutivo() throws Exception {
        for (int i = 4; i >= 1; i--) {
            recebimentoCestaRepository.save(RecebedorFixtures.recebimento(
                    recebedor, MES_ATUAL.minusMonths(i), StatusRecebimento.RECEBEU));
        }

        mockMvc.perform(post("/api/recebimento-cesta/{id}", recebedor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cestaJson(null)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Limite de 4 meses consecutivos atingido"));
    }

    @Test
    @DisplayName("corpo inválido responde 400")
    void corpoInvalido() throws Exception {
        mockMvc.perform(post("/api/recebimento-cesta/{id}", recebedor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ano\":2026,\"mes\":\"JANEIRO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("registrar para recebedor inexistente responde 404")
    void registraInexistente() throws Exception {
        mockMvc.perform(post("/api/recebimento-cesta/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cestaJson(null)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Recebedor not found")));
    }

    private String cestaJson(String observacao) {
        String obs = observacao == null ? "null" : "\"" + observacao + "\"";
        return """
                {"ano":2026,"mes":"JANEIRO","status":"RECEBEU","observacao":%s}
                """.formatted(obs);
    }
}
