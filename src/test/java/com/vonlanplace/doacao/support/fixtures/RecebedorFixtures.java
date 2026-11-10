package com.vonlanplace.doacao.support.fixtures;

import com.vonlanplace.doacao.recebedor.OrgaoEmissorRG;
import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorCreateDTO;
import com.vonlanplace.doacao.recebedor.RecebedorUpdateDTO;
import com.vonlanplace.doacao.recebedor.cesta.MesRecebimento;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCesta;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaCreateDTO;
import com.vonlanplace.doacao.recebedor.cesta.SnapshotRecebedor;
import com.vonlanplace.doacao.recebedor.cesta.StatusRecebimento;
import com.vonlanplace.doacao.recebedor.form.AuxilioFormDTO;
import com.vonlanplace.doacao.recebedor.form.DependenteFormDTO;
import com.vonlanplace.doacao.recebedor.form.DespesaFormDTO;
import com.vonlanplace.doacao.recebedor.form.RecebedorFormDTO;
import com.vonlanplace.doacao.recebedor.form.ResidenciaFormDTO;
import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import com.vonlanplace.doacao.recebedor.residencia.TipoOcupacao;
import com.vonlanplace.doacao.recebedor.residencia.TipoResidencia;
import com.vonlanplace.doacao.support.DadosUnicos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

/**
 * Dados válidos reutilizáveis pelos testes.
 *
 * <p>Os builders não escondem o domínio: cada método devolve um objeto pronto
 * para o cenário mais comum, e o teste altera apenas o campo que está
 * exercitando.</p>
 */
public final class RecebedorFixtures {

    private RecebedorFixtures() {
    }

    public static Residencia residencia() {
        return residencia("Rua das Flores", "Recife");
    }

    public static Residencia residencia(String logradouro, String cidade) {
        Residencia residencia = new Residencia();
        residencia.setLogradouro(logradouro);
        residencia.setNumero("100");
        residencia.setCep("01001000");
        residencia.setCidade(cidade);
        residencia.setBairro("Santo Amaro");
        residencia.setNumMoradores(2);
        residencia.setNumTrabalhadores(1);
        residencia.setTipoOcupacao(TipoOcupacao.LOCACAO);
        residencia.setTipoResidencia(TipoResidencia.ALVENARIA_TRADICIONAL);
        return residencia;
    }

    public static Recebedor recebedor(Residencia residencia) {
        Recebedor recebedor = new Recebedor();
        recebedor.setNomeCompleto("Maria da Silva");
        recebedor.setCpf(DadosUnicos.cpf());
        recebedor.setDataNascimento(LocalDate.of(1980, 5, 20));
        recebedor.setLeEscreve(true);
        recebedor.setPix(DadosUnicos.pix());
        recebedor.setResidencia(residencia);
        return recebedor;
    }

    public static RecebedorCreateDTO createDTO(UUID residenciaId) {
        return new RecebedorCreateDTO(
                "João da Costa",
                DadosUnicos.cpf(),
                "12345",
                OrgaoEmissorRG.SSP,
                LocalDate.of(1990, 3, 10),
                "81999999999",
                null,
                true,
                "joao@email.com",
                DadosUnicos.pix(),
                false,
                false,
                null,
                false,
                "observação",
                residenciaId
        );
    }

    public static RecebedorUpdateDTO updateDTO(UUID residenciaId) {
        return new RecebedorUpdateDTO(
                "João da Costa",
                DadosUnicos.cpf(),
                "12345",
                OrgaoEmissorRG.SSP,
                LocalDate.of(1990, 3, 10),
                "81999999999",
                null,
                true,
                "joao@email.com",
                DadosUnicos.pix(),
                false,
                false,
                null,
                false,
                "observação",
                residenciaId
        );
    }

    public static RecebedorFormDTO formularioValido() {
        RecebedorFormDTO form = new RecebedorFormDTO();
        form.setNomeCompleto("Ana Souza");
        form.setCpf(DadosUnicos.cpf());
        form.setRgNum("123456");
        form.setDataNascimento(LocalDate.of(1990, 1, 1));
        form.setLeEscreve(true);
        form.setNecessitaPsicologo(true);
        form.setStatusCesta(StatusRecebimento.RECEBEU);
        form.setParecerEntrevistador(true);
        form.setComentarios("Primeira visita.");
        form.setResidencia(residenciaForm());
        form.getDependentes().add(dependenteForm("Filho", 5, LocalDate.of(2018, 1, 1)));
        form.getAuxilios().add(auxilioForm("200.00", "Bolsa Família", "Auxílio mensal"));
        form.getDespesas().add(despesaForm("300.00", "Aluguel", "Aluguel da casa"));
        return form;
    }

    public static ResidenciaFormDTO residenciaForm() {
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
        return residencia;
    }

    public static DependenteFormDTO dependenteForm(String nome, int idade, LocalDate nascimento) {
        DependenteFormDTO dependente = new DependenteFormDTO();
        dependente.setNome(nome);
        dependente.setIdade(idade);
        dependente.setDataNascimento(nascimento);
        return dependente;
    }

    public static AuxilioFormDTO auxilioForm(String valor, String fonte, String descricao) {
        AuxilioFormDTO auxilio = new AuxilioFormDTO();
        auxilio.setValor(valor == null ? null : new BigDecimal(valor));
        auxilio.setFonte(fonte);
        auxilio.setDescricao(descricao);
        return auxilio;
    }

    public static DespesaFormDTO despesaForm(String valor, String fonte, String descricao) {
        DespesaFormDTO despesa = new DespesaFormDTO();
        despesa.setValor(valor == null ? null : new BigDecimal(valor));
        despesa.setFonte(fonte);
        despesa.setDescricao(descricao);
        return despesa;
    }

    public static SnapshotRecebedor snapshot() {
        SnapshotRecebedor snapshot = new SnapshotRecebedor();
        snapshot.setNomeCompleto("Maria da Silva");
        snapshot.setCpf("11111111111");
        snapshot.setRgNum("12345");
        snapshot.setRgEmissor(OrgaoEmissorRG.SSP);
        snapshot.setDataNascimento(LocalDate.of(1980, 5, 20));
        snapshot.setTelefonePessoal("81999999999");
        snapshot.setLeEscreve(true);
        snapshot.setEmail("maria@email.com");
        snapshot.setPix("maria@pix.com");
        snapshot.setLogradouro("Rua das Flores");
        snapshot.setNumero("100");
        snapshot.setBairro("Santo Amaro");
        snapshot.setCidade("Recife");
        snapshot.setCep("01001000");
        return snapshot;
    }

    public static RecebimentoCesta recebimento(
            Recebedor recebedor,
            YearMonth mes,
            StatusRecebimento status
    ) {
        RecebimentoCesta recebimento = new RecebimentoCesta();
        recebimento.setRecebedor(recebedor);
        recebimento.setDataReferencia(mes.atDay(1));
        recebimento.setStatus(status);
        recebimento.setSnapshot(snapshot());
        return recebimento;
    }

    public static RecebimentoCestaCreateDTO createCesta(
            YearMonth mes,
            StatusRecebimento status
    ) {
        return new RecebimentoCestaCreateDTO(
                mes.getYear(),
                MesRecebimento.de(mes.getMonthValue()),
                status,
                null,
                null
        );
    }
}
