package com.vonlanplace.doacao.recebedor.form;

import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;
import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioResponseDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioService;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaService;
import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import com.vonlanplace.doacao.recebedor.dependente.DependenteResponseDTO;
import com.vonlanplace.doacao.recebedor.dependente.DependenteService;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaService;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Testes de integração do formulário unificado de recebedor.
 */
@SpringBootTest
@Transactional
@DisplayName("RecebedorFormService")
class RecebedorFormServiceTest {

    @Autowired
    private RecebedorFormService recebedorFormService;

    @Autowired
    private RecebedorService recebedorService;

    @Autowired
    private ResidenciaService residenciaService;

    @Autowired
    private DependenteService dependenteService;

    @Autowired
    private AuxilioService auxilioService;

    @Autowired
    private DespesaService despesaService;

    @Autowired
    private RecebimentoCestaService recebimentoCestaService;

    @Test
    @DisplayName("cria recebedor, residência, dependente, auxílio, despesa e status da cesta")
    void criaTudoEmUmaTransacao() {
        RecebedorResponseDTO criado = recebedorFormService.criar(
                formularioValido("11122233344", "Ana Souza"));

        assertThat(criado.id()).isNotNull();
        assertThat(recebedorService.findById(criado.id()).nomeCompleto()).isEqualTo("Ana Souza");
        assertThat(residenciaService.findById(criado.residencia().id()).logradouro())
                .isEqualTo("Rua A");
        assertThat(dependenteService.findByRecebedor(criado.id()))
                .extracting(DependenteResponseDTO::nome)
                .containsExactly("Filho");
        assertThat(auxilioService.findByRecebedor(criado.id()))
                .extracting(AuxilioResponseDTO::fonte)
                .containsExactly("Bolsa Família");
        assertThat(despesaService.findByResidencia(criado.residencia().id()))
                .extracting(DespesaResponseDTO::fonte)
                .containsExactly("Aluguel");
        assertThat(recebimentoCestaService.statusAtual(criado.id(), YearMonth.now()))
                .isEqualTo(StatusRecebimento.RECEBEU);
        assertThat(recebedorFormService.carregarFormulario(criado.id()).getStatusCesta())
                .isEqualTo(StatusRecebimento.RECEBEU);
    }

    @Test
    @DisplayName("atualiza dados e sincroniza listas: edita, cria e remove filhos")
    void atualizaSincronizandoFilhos() {
        RecebedorResponseDTO criado = recebedorFormService.criar(
                formularioValido("11122233344", "Ana Souza"));

        RecebedorFormDTO edicao = recebedorFormService.carregarFormulario(criado.id());
        edicao.setNomeCompleto("Ana Souza Editada");
        edicao.getResidencia().setLogradouro("Rua Nova");
        edicao.getDependentes().get(0).setNome("Filho Editado");

        DependenteFormDTO novoDependente = new DependenteFormDTO();
        novoDependente.setNome("Segundo Filho");
        novoDependente.setIdade(2);
        novoDependente.setDataNascimento(LocalDate.of(2024, 1, 1));
        edicao.getDependentes().add(novoDependente);

        edicao.getAuxilios().clear();

        edicao.getDespesas().clear();
        DespesaFormDTO novaDespesa = new DespesaFormDTO();
        novaDespesa.setValor(new BigDecimal("50.00"));
        novaDespesa.setFonte("Água");
        novaDespesa.setDescricao("Conta mensal");
        edicao.getDespesas().add(novaDespesa);

        recebedorFormService.atualizar(criado.id(), edicao);

        assertThat(recebedorService.findById(criado.id()).nomeCompleto())
                .isEqualTo("Ana Souza Editada");
        assertThat(residenciaService.findById(criado.residencia().id()).logradouro())
                .isEqualTo("Rua Nova");
        assertThat(dependenteService.findByRecebedor(criado.id()))
                .extracting(DependenteResponseDTO::nome)
                .containsExactlyInAnyOrder("Filho Editado", "Segundo Filho");
        assertThat(auxilioService.findByRecebedor(criado.id())).isEmpty();
        assertThat(despesaService.findByResidencia(criado.residencia().id()))
                .extracting(DespesaResponseDTO::fonte)
                .containsExactly("Água");
    }

    @Test
    @DisplayName("recusa mover dependente de outro recebedor")
    void recusaDependenteDeOutroRecebedor() {
        RecebedorResponseDTO primeiro = recebedorFormService.criar(
                formularioValido("11122233344", "Ana Souza"));
        RecebedorResponseDTO segundo = recebedorFormService.criar(
                formularioValido("55566677788", "Bia Lima"));

        DependenteResponseDTO dependenteDoSegundo =
                dependenteService.findByRecebedor(segundo.id()).get(0);

        RecebedorFormDTO edicao = recebedorFormService.carregarFormulario(primeiro.id());
        DependenteFormDTO invasor = new DependenteFormDTO();
        invasor.setId(dependenteDoSegundo.id());
        invasor.setNome("Tentativa");
        invasor.setIdade(10);
        edicao.setDependentes(List.of(invasor));

        assertThatThrownBy(() -> recebedorFormService.atualizar(primeiro.id(), edicao))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dependente não pertence");
    }

    private RecebedorFormDTO formularioValido(String cpf, String nome) {
        RecebedorFormDTO form = new RecebedorFormDTO();
        form.setNomeCompleto(nome);
        form.setCpf(cpf);
        form.setRgNum("123456");
        form.setDataNascimento(LocalDate.of(1990, 1, 1));
        form.setLeEscreve(true);
        form.setNecessitaPsicologo(true);
        form.setStatusCesta(StatusRecebimento.RECEBEU);
        form.setParecerEntrevistador(true);
        form.setComentarios("Primeira visita.");

        ResidenciaFormDTO residencia = new ResidenciaFormDTO();
        residencia.setLogradouro("Rua A");
        residencia.setNumero("10");
        residencia.setCep("01001000");
        residencia.setCidade("São Paulo");
        residencia.setBairro("Centro");
        residencia.setNumMoradores(2);
        residencia.setNumTrabalhadores(1);
        residencia.setTipoOcupacao(TipoOcupacao.PROPRIEDADE);
        residencia.setTipoResidencia(TipoResidencia.ALVENARIA_TRADICIONAL);
        form.setResidencia(residencia);

        DependenteFormDTO dependente = new DependenteFormDTO();
        dependente.setNome("Filho");
        dependente.setIdade(5);
        dependente.setDataNascimento(LocalDate.of(2018, 1, 1));
        form.getDependentes().add(dependente);

        AuxilioFormDTO auxilio = new AuxilioFormDTO();
        auxilio.setValor(new BigDecimal("200.00"));
        auxilio.setFonte("Bolsa Família");
        auxilio.setDescricao("Auxílio mensal");
        form.getAuxilios().add(auxilio);

        DespesaFormDTO despesa = new DespesaFormDTO();
        despesa.setValor(new BigDecimal("300.00"));
        despesa.setFonte("Aluguel");
        despesa.setDescricao("Aluguel da casa");
        form.getDespesas().add(despesa);

        return form;
    }
}
