package com.vonlanplace.doacao.web;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import com.vonlanplace.doacao.recebedor.auxilio.Auxilio;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioRepository;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import com.vonlanplace.doacao.recebedor.residencia.despesa.Despesa;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Confere que as páginas de dependente, despesa e auxílio renderizam.
 *
 * <p><strong>O motivo de teste de renderização estar no plano.</strong>Cada um
 * dos três é um sub-registro com select de pai: o {@code th:field} do select
 * precisa do id do pai no model, o campo monetário precisa do
 * {@code #numbers.formatDecimal} certo. Nada disso é conferido pelo compilador —
 * um select sem opções ou um {@code th:each} sobre o {@code Page} em vez de
 * {@code page.content} só falha quando a página é pedida.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Renderização das páginas de dependente, despesa e auxílio")
class FilhosRecebedorPaginasTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private AuxilioRepository auxilioRepository;

    private Recebedor recebedor;

    /**
     * Cria o cenário com recebedor, residência, despesa e auxílio persistidos.
     */
    @BeforeEach
    void setUp() {
        Residencia residencia = new Residencia();
        residencia.setLogradouro("Rua das Flores");
        residencia.setNumero("100");
        residencia.setCep("01001000");
        residencia.setCidade("Recife");
        residencia.setBairro("Santo Amaro");
        residencia.setNumMoradores(2);
        residencia.setNumTrabalhadores(0);
        residencia.setTipoOcupacao(TipoOcupacao.LOCACAO);
        residencia.setTipoResidencia(TipoResidencia.ALVENARIA_TRADICIONAL);
        residencia = residenciaRepository.save(residencia);

        recebedor = new Recebedor();
        recebedor.setNomeCompleto("Maria da Silva");
        recebedor.setCpf("11111111111");
        recebedor.setDataNascimento(LocalDate.of(1980, 5, 20));
        recebedor.setLeEscreve(true);
        recebedor.setResidencia(residencia);
        recebedor = recebedorRepository.save(recebedor);

        Despesa despesa = new Despesa();
        despesa.setValor(new BigDecimal("300.00"));
        despesa.setFonte("Aluguel");
        despesa.setResidencia(residencia);
        despesaRepository.save(despesa);

        Auxilio auxilio = new Auxilio();
        auxilio.setValor(new BigDecimal("200.00"));
        auxilio.setFonte("Bolsa Família");
        auxilio.setRecebedor(recebedor);
        auxilioRepository.save(auxilio);
    }

    @Test
    @DisplayName("a listagem de dependente renderiza")
    void listarDependente() throws Exception {
        mockMvc.perform(get("/dependente/listar").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("dependente/listar"))
                .andExpect(content().string(containsString("Nenhum dependente cadastrado")));
    }

    @Test
    @DisplayName("o formulário de dependente mostra o select de recebedor")
    void formDependente() throws Exception {
        mockMvc.perform(get("/dependente/create").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("dependente/form"))
                .andExpect(content().string(containsString("Recebedor")))
                .andExpect(content().string(containsString("Nome completo")));
    }

    @Test
    @DisplayName("a listagem de despesa renderiza com as despesas do cenário")
    void listarDespesa() throws Exception {
        mockMvc.perform(get("/despesa/listar").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("despesa/listar"))
                .andExpect(content().string(containsString("Despesas cadastradas")))
                .andExpect(content().string(containsString("R$ 300,00")));
    }

    @Test
    @DisplayName("o formulário de despesa mostra o select de residência")
    void formDespesa() throws Exception {
        mockMvc.perform(get("/despesa/create").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("despesa/form"))
                .andExpect(content().string(containsString("Residência")))
                .andExpect(content().string(containsString("Valor mensal")));
    }

    @Test
    @DisplayName("a listagem de auxílio renderiza com os auxílios do cenário")
    void listarAuxilio() throws Exception {
        mockMvc.perform(get("/auxilio/listar").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("auxilio/listar"))
                .andExpect(content().string(containsString("Auxílios cadastrados")))
                .andExpect(content().string(containsString("R$ 200,00")));
    }

    @Test
    @DisplayName("o formulário de auxílio mostra o select de recebedor")
    void formAuxilio() throws Exception {
        mockMvc.perform(get("/auxilio/create").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("auxilio/form"))
                .andExpect(content().string(containsString("Recebedor")))
                .andExpect(content().string(containsString("Valor mensal")));
    }

    @Test
    @DisplayName("o detalhe do recebedor mostra a somatória de despesas e auxílios")
    void detalheRecebedorComSomatoria() throws Exception {
        mockMvc.perform(get("/recebedor/detalhe/{id}", recebedor.getId()).session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("recebedor/detalhe"))
                .andExpect(content().string(containsString("R$ 300,00")))
                .andExpect(content().string(containsString("R$ 200,00")))
                .andExpect(content().string(containsString("Situação financeira para a cesta")));
    }

    @Test
    @DisplayName("resolução de dependente inexistente responde 404 HTML")
    void dependenteInexistente() throws Exception {
        mockMvc.perform(get("/dependente/editar/00000000-0000-0000-0000-000000000000").session(SessaoAutenticacao.logada()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("erro/404"))
                .andExpect(content().string(not(containsString("problem+json"))));
    }
}