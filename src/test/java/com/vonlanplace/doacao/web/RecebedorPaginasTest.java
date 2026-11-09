package com.vonlanplace.doacao.web;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import com.vonlanplace.doacao.recebedor.auxilio.Auxilio;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioRepository;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaService;
import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import com.vonlanplace.doacao.recebedor.dependente.Dependente;
import com.vonlanplace.doacao.recebedor.dependente.DependenteRepository;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaRepository;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import com.vonlanplace.doacao.recebedor.residencia.despesa.Despesa;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Confere a renderização e o fluxo POST do formulário único de recebedor.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Páginas de recebedor")
class RecebedorPaginasTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecebedorRepository recebedorRepository;

    @Autowired
    private ResidenciaRepository residenciaRepository;

    @Autowired
    private DependenteRepository dependenteRepository;

    @Autowired
    private AuxilioRepository auxilioRepository;

    @Autowired
    private DespesaRepository despesaRepository;

    @Autowired
    private RecebimentoCestaService recebimentoCestaService;

    @Test
    @DisplayName("a listagem de recebedor renderiza")
    void listarRecebedores() throws Exception {
        mockMvc.perform(get("/recebedor/listar"))
                .andExpect(status().isOk())
                .andExpect(view().name("recebedor/listar"))
                .andExpect(content().string(containsString("Nenhum recebedor cadastrado")));
    }

    @Test
    @DisplayName("o formulário de cadastro tem as cinco abas e os campos do cadastro completo")
    void formHasAbasECampos() throws Exception {
        mockMvc.perform(get("/recebedor/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("recebedor/form"))
                .andExpect(content().string(containsString("Pessoal")))
                .andExpect(content().string(containsString("Residencial")))
                .andExpect(content().string(containsString("Dependentes")))
                .andExpect(content().string(containsString("Econômicos")))
                .andExpect(content().string(containsString("Outros")))
                .andExpect(content().string(containsString("Cesta do mês")))
                .andExpect(content().string(containsString("Parecer do entrevistador")))
                .andExpect(content().string(containsString("Comentários da entrevista")))
                .andExpect(content().string(containsString("name=\"residencia.logradouro\"")))
                .andExpect(content().string(containsString("Adicionar dependente")))
                .andExpect(content().string(containsString("Adicionar despesa")))
                .andExpect(content().string(containsString("Adicionar auxílio")));
    }

    @Test
    @DisplayName("o formulário não tem mais select de residência existente")
    void formNaoTemSelectDeResidencia() throws Exception {
        mockMvc.perform(get("/recebedor/create"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("name=\"residenciaId\""))));
    }

    @Test
    @DisplayName("a edição carrega residência, dependentes, auxílios e despesas")
    void editarCarregaDadosAninhados() throws Exception {
        Recebedor recebedor = criarCenario();

        mockMvc.perform(get("/recebedor/editar/{id}", recebedor.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("recebedor/form"))
                .andExpect(content().string(containsString("Maria da Silva")))
                .andExpect(content().string(containsString("Rua das Flores")))
                .andExpect(content().string(containsString("name=\"dependentes[0].nome\"")))
                .andExpect(content().string(containsString("Maria dos Santos")))
                .andExpect(content().string(containsString("name=\"auxilios[0].fonte\"")))
                .andExpect(content().string(containsString("Bolsa Fam")))
                .andExpect(content().string(containsString("name=\"despesas[0].fonte\"")))
                .andExpect(content().string(containsString("Aluguel")));
    }

    @Test
    @DisplayName("o detalhe mostra despesas e auxílios sem links para telas separadas")
    void detalheMostraDespesasEAuxilios() throws Exception {
        Recebedor recebedor = criarCenario();

        mockMvc.perform(get("/recebedor/detalhe/{id}", recebedor.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("recebedor/detalhe"))
                .andExpect(content().string(containsString("Situação financeira para a cesta")))
                .andExpect(content().string(containsString("R$ 300,00")))
                .andExpect(content().string(containsString("R$ 200,00")))
                .andExpect(content().string(containsString("Aluguel")))
                .andExpect(content().string(containsString("Bolsa Família")))
                .andExpect(content().string(not(containsString("href=\"/despesa/listar\""))))
                .andExpect(content().string(not(containsString("href=\"/auxilio/listar\""))));
    }

    @Test
    @DisplayName("o POST do formulário cria recebedor, residência e filhos")
    void cadastroCompletoCriaTudo() throws Exception {
        mockMvc.perform(cadastroValido()
                        .param("dependentes[0].nome", "Filho")
                        .param("dependentes[0].idade", "5")
                        .param("dependentes[0].dataNascimento", "2018-01-01")
                        .param("auxilios[0].valor", "200.00")
                        .param("auxilios[0].fonte", "Bolsa Família")
                        .param("auxilios[0].descricao", "Auxílio mensal")
                        .param("despesas[0].valor", "300.00")
                        .param("despesas[0].fonte", "Aluguel")
                        .param("despesas[0].descricao", "Aluguel da casa"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/recebedor/detalhe/*"));

        Recebedor recebedor = recebedorRepository.findByCpf("11122233344").orElseThrow();
        assertThat(recebedor.getNomeCompleto()).isEqualTo("Ana Souza");
        assertThat(recebedor.getResidencia().getLogradouro()).isEqualTo("Rua A");
        assertThat(residenciaRepository.count()).isEqualTo(1);
        assertThat(dependenteRepository.count()).isEqualTo(1);
        assertThat(auxilioRepository.count()).isEqualTo(1);
        assertThat(despesaRepository.count()).isEqualTo(1);
        assertThat(recebimentoCestaService.statusAtual(recebedor.getId(), YearMonth.now()))
                .isEqualTo(StatusRecebimento.RECEBEU);
    }

    @Test
    @DisplayName("linhas completamente vazias são ignoradas no POST")
    void linhaVaziaEhIgnorada() throws Exception {
        mockMvc.perform(cadastroValido()
                        .param("dependentes[0].nome", "")
                        .param("dependentes[0].idade", "")
                        .param("dependentes[0].dataNascimento", "")
                        .param("auxilios[0].valor", "")
                        .param("auxilios[0].fonte", "")
                        .param("despesas[0].valor", "")
                        .param("despesas[0].fonte", ""))
                .andExpect(status().is3xxRedirection());

        assertThat(dependenteRepository.count()).isZero();
        assertThat(auxilioRepository.count()).isZero();
        assertThat(despesaRepository.count()).isZero();
    }

    @Test
    @DisplayName("linha parcialmente preenchida volta ao formulário com erro")
    void linhaParcialVoltaComErro() throws Exception {
        mockMvc.perform(cadastroValido()
                        .param("auxilios[0].fonte", "ONG"))
                .andExpect(status().isOk())
                .andExpect(view().name("recebedor/form"))
                .andExpect(content().string(containsString("O valor é obrigatório")));
    }

    @Test
    @DisplayName("recebedor inexistente responde 404 em HTML")
    void recebedorInexistente() throws Exception {
        mockMvc.perform(get("/recebedor/detalhe/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("erro/404"))
                .andExpect(content().string(not(containsString("problem+json"))));
    }

    private MockHttpServletRequestBuilder cadastroValido() {
        return post("/recebedor/create")
                .param("nomeCompleto", "Ana Souza")
                .param("cpf", "11122233344")
                .param("rgNum", "123456")
                .param("dataNascimento", "1990-01-01")
                .param("leEscreve", "true")
                .param("necessitaJuridico", "false")
                .param("necessitaPsicologo", "true")
                .param("statusCesta", "RECEBEU")
                .param("parecerEntrevistador", "true")
                .param("comentarios", "Primeira visita.")
                .param("residencia.logradouro", "Rua A")
                .param("residencia.numero", "10")
                .param("residencia.cep", "01001000")
                .param("residencia.cidade", "São Paulo")
                .param("residencia.bairro", "Centro")
                .param("residencia.numMoradores", "2")
                .param("residencia.numTrabalhadores", "1")
                .param("residencia.tipoOcupacao", "PROPRIEDADE")
                .param("residencia.tipoResidencia", "ALVENARIA_TRADICIONAL");
    }

    private Recebedor criarCenario() {
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

        Recebedor recebedor = new Recebedor();
        recebedor.setNomeCompleto("Maria da Silva");
        recebedor.setCpf("11111111111");
        recebedor.setDataNascimento(LocalDate.of(1980, 5, 20));
        recebedor.setLeEscreve(true);
        recebedor.setResidencia(residencia);
        recebedor = recebedorRepository.save(recebedor);

        Dependente dependente = new Dependente();
        dependente.setNome("Maria dos Santos");
        dependente.setIdade(7);
        dependente.setDataNascimento(LocalDate.of(2018, 1, 1));
        dependente.setRecebedor(recebedor);
        dependenteRepository.save(dependente);

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

        return recebedor;
    }
}
