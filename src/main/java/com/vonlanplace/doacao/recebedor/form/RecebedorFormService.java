package com.vonlanplace.doacao.recebedor.form;

import com.vonlanplace.doacao.recebedor.RecebedorResponseDTO;
import com.vonlanplace.doacao.recebedor.RecebedorService;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioResponseDTO;
import com.vonlanplace.doacao.recebedor.auxilio.AuxilioService;
import com.vonlanplace.doacao.recebedor.cesta.RecebimentoCestaService;
import com.vonlanplace.doacao.recebedor.dependente.DependenteResponseDTO;
import com.vonlanplace.doacao.recebedor.dependente.DependenteService;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.ResidenciaService;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaResponseDTO;
import com.vonlanplace.doacao.recebedor.residencia.despesa.DespesaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Orquestra o cadastro e a edição do recebedor em um único formulário.
 *
 * <p><strong>Criação sempre cria uma residência nova.</strong> O recebedor e seus
 * dependentes, auxílios e despesas são gravados na mesma transação: se qualquer
 * etapa falhar, nada fica pela metade.</p>
 *
 * <p><strong>Edição sincroniza as listas.</strong> Linhas com {@code id} são
 * atualizadas, linhas sem {@code id} são criadas e linhas ausentes do formulário
 * são apagadas. Antes de alterar ou apagar, o serviço confere se o registro
 * pertence ao recebedor ou à residência atual, para que um POST manipulado não
 * mova dados de outro cadastro.</p>
 */
@Service
public class RecebedorFormService {

    private final RecebedorService recebedorService;
    private final ResidenciaService residenciaService;
    private final DependenteService dependenteService;
    private final AuxilioService auxilioService;
    private final DespesaService despesaService;
    private final RecebimentoCestaService recebimentoCestaService;
    private final RecebedorFormMapper mapper;
    private final Clock clock;

    /**
     * Construtor com injeção de dependências.
     *
     * @param recebedorService  serviço de recebedores
     * @param residenciaService serviço de residências
     * @param dependenteService serviço de dependentes
     * @param auxilioService    serviço de auxílios
     * @param despesaService    serviço de despesas
     * @param recebimentoCestaService serviço do histórico de cestas
     * @param mapper            conversor do formulário
     * @param clock             relógio da aplicação
     */
    public RecebedorFormService(
            RecebedorService recebedorService,
            ResidenciaService residenciaService,
            DependenteService dependenteService,
            AuxilioService auxilioService,
            DespesaService despesaService,
            RecebimentoCestaService recebimentoCestaService,
            RecebedorFormMapper mapper,
            Clock clock
    ) {
        this.recebedorService = recebedorService;
        this.residenciaService = residenciaService;
        this.dependenteService = dependenteService;
        this.auxilioService = auxilioService;
        this.despesaService = despesaService;
        this.recebimentoCestaService = recebimentoCestaService;
        this.mapper = mapper;
        this.clock = clock;
    }

    /**
     * Carrega o formulário de edição de um recebedor.
     *
     * @param id identificador do recebedor
     * @return formulário preenchido com os dados atuais
     */
    @Transactional(readOnly = true)
    public RecebedorFormDTO carregarFormulario(UUID id) {
        RecebedorResponseDTO recebedor = recebedorService.findById(id);
        UUID residenciaId = recebedor.residencia().id();
        List<DependenteResponseDTO> dependentes = dependenteService.findByRecebedor(id);
        List<AuxilioResponseDTO> auxilios = auxilioService.findByRecebedor(id);
        List<DespesaResponseDTO> despesas = despesaService.findByResidencia(residenciaId);
        return mapper.toFormDTO(
                recebedor,
                dependentes,
                auxilios,
                despesas,
                recebimentoCestaService.statusAtual(id, YearMonth.now(clock))
        );
    }

    /**
     * Cadastra recebedor, residência e filhos em uma única transação.
     *
     * @param form dados do formulário
     * @return recebedor criado
     */
    @Transactional
    public RecebedorResponseDTO criar(RecebedorFormDTO form) {
        ResidenciaResponseDTO residencia = residenciaService.create(
                mapper.toResidenciaCreateDTO(form.getResidencia()));
        RecebedorResponseDTO recebedor = recebedorService.create(
                mapper.toCreateDTO(form, residencia.id()));

        criarDependentes(form.getDependentes(), recebedor.id());
        criarAuxilios(form.getAuxilios(), recebedor.id());
        criarDespesas(form.getDespesas(), residencia.id());

        return recebedor;
    }

    /**
     * Atualiza recebedor, residência e sincroniza os filhos em uma única transação.
     *
     * @param id   identificador do recebedor
     * @param form dados do formulário
     * @return recebedor atualizado
     */
    @Transactional
    public RecebedorResponseDTO atualizar(UUID id, RecebedorFormDTO form) {
        RecebedorResponseDTO atual = recebedorService.findById(id);
        UUID residenciaId = atual.residencia().id();

        residenciaService.update(residenciaId, mapper.toResidenciaUpdateDTO(form.getResidencia()));
        RecebedorResponseDTO recebedor = recebedorService.update(
                id, mapper.toUpdateDTO(form, residenciaId));

        sincronizarDependentes(id, form.getDependentes());
        sincronizarAuxilios(id, form.getAuxilios());
        sincronizarDespesas(residenciaId, form.getDespesas());

        return recebedor;
    }

    private void criarDependentes(List<DependenteFormDTO> linhas, UUID recebedorId) {
        for (DependenteFormDTO linha : linhas) {
            if (linhaPreenchida(linha)) {
                dependenteService.create(mapper.toDependenteCreateDTO(linha, recebedorId));
            }
        }
    }

    private void criarAuxilios(List<AuxilioFormDTO> linhas, UUID recebedorId) {
        for (AuxilioFormDTO linha : linhas) {
            if (linhaPreenchida(linha)) {
                auxilioService.create(mapper.toAuxilioCreateDTO(linha, recebedorId));
            }
        }
    }

    private void criarDespesas(List<DespesaFormDTO> linhas, UUID residenciaId) {
        for (DespesaFormDTO linha : linhas) {
            if (linhaPreenchida(linha)) {
                despesaService.create(mapper.toDespesaCreateDTO(linha, residenciaId));
            }
        }
    }

    private void sincronizarDependentes(UUID recebedorId, List<DependenteFormDTO> linhas) {
        List<DependenteResponseDTO> atuais = dependenteService.findByRecebedor(recebedorId);
        Set<UUID> idsAtuais = idsDependentes(atuais);
        Set<UUID> mantidos = new HashSet<>();

        for (DependenteFormDTO linha : linhas) {
            if (!linhaPreenchida(linha)) {
                continue;
            }
            if (linha.getId() == null) {
                dependenteService.create(mapper.toDependenteCreateDTO(linha, recebedorId));
                continue;
            }
            if (!idsAtuais.contains(linha.getId())) {
                throw new IllegalArgumentException("Dependente não pertence a este recebedor");
            }
            dependenteService.update(
                    linha.getId(), mapper.toDependenteUpdateDTO(linha, recebedorId));
            mantidos.add(linha.getId());
        }

        for (DependenteResponseDTO atual : atuais) {
            if (!mantidos.contains(atual.id())) {
                dependenteService.delete(atual.id());
            }
        }
    }

    private void sincronizarAuxilios(UUID recebedorId, List<AuxilioFormDTO> linhas) {
        List<AuxilioResponseDTO> atuais = auxilioService.findByRecebedor(recebedorId);
        Set<UUID> idsAtuais = idsAuxilios(atuais);
        Set<UUID> mantidos = new HashSet<>();

        for (AuxilioFormDTO linha : linhas) {
            if (!linhaPreenchida(linha)) {
                continue;
            }
            if (linha.getId() == null) {
                auxilioService.create(mapper.toAuxilioCreateDTO(linha, recebedorId));
                continue;
            }
            if (!idsAtuais.contains(linha.getId())) {
                throw new IllegalArgumentException("Auxílio não pertence a este recebedor");
            }
            auxilioService.update(
                    linha.getId(), mapper.toAuxilioUpdateDTO(linha, recebedorId));
            mantidos.add(linha.getId());
        }

        for (AuxilioResponseDTO atual : atuais) {
            if (!mantidos.contains(atual.id())) {
                auxilioService.delete(atual.id());
            }
        }
    }

    private void sincronizarDespesas(UUID residenciaId, List<DespesaFormDTO> linhas) {
        List<DespesaResponseDTO> atuais = despesaService.findByResidencia(residenciaId);
        Set<UUID> idsAtuais = idsDespesas(atuais);
        Set<UUID> mantidos = new HashSet<>();

        for (DespesaFormDTO linha : linhas) {
            if (!linhaPreenchida(linha)) {
                continue;
            }
            if (linha.getId() == null) {
                despesaService.create(mapper.toDespesaCreateDTO(linha, residenciaId));
                continue;
            }
            if (!idsAtuais.contains(linha.getId())) {
                throw new IllegalArgumentException("Despesa não pertence a esta residência");
            }
            despesaService.update(
                    linha.getId(), mapper.toDespesaUpdateDTO(linha, residenciaId));
            mantidos.add(linha.getId());
        }

        for (DespesaResponseDTO atual : atuais) {
            if (!mantidos.contains(atual.id())) {
                despesaService.delete(atual.id());
            }
        }
    }

    private boolean linhaPreenchida(DependenteFormDTO linha) {
        return linha.getId() != null
                || !textoVazio(linha.getNome())
                || linha.getIdade() != 0
                || linha.getDataNascimento() != null;
    }

    private boolean linhaPreenchida(AuxilioFormDTO linha) {
        return linha.getId() != null
                || linha.getValor() != null
                || !textoVazio(linha.getFonte())
                || !textoVazio(linha.getDescricao());
    }

    private boolean linhaPreenchida(DespesaFormDTO linha) {
        return linha.getId() != null
                || linha.getValor() != null
                || !textoVazio(linha.getFonte())
                || !textoVazio(linha.getDescricao());
    }

    private boolean textoVazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private Set<UUID> idsDependentes(List<DependenteResponseDTO> atuais) {
        Set<UUID> ids = new HashSet<>();
        atuais.forEach(dependente -> ids.add(dependente.id()));
        return ids;
    }

    private Set<UUID> idsAuxilios(List<AuxilioResponseDTO> atuais) {
        Set<UUID> ids = new HashSet<>();
        atuais.forEach(auxilio -> ids.add(auxilio.id()));
        return ids;
    }

    private Set<UUID> idsDespesas(List<DespesaResponseDTO> atuais) {
        Set<UUID> ids = new HashSet<>();
        atuais.forEach(despesa -> ids.add(despesa.id()));
        return ids;
    }
}
