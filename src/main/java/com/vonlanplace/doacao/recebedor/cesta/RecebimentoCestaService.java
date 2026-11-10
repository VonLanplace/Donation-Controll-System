package com.vonlanplace.doacao.recebedor.cesta;

import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Regras de negócio do recebimento de cesta.
 *
 * <p><strong>Limite de quatro meses consecutivos.</strong> O quinto mês seguido com
 * {@code RECEBEU} é bloqueado. {@code RECUSOU} não entra na contagem e funciona como
 * intervalo, reiniciando a sequência.</p>
 *
 * <p><strong>Idempotência no mês.</strong> Registrar de novo o mesmo status para o
 * mesmo mês atualiza o registro existente em vez de criar outro. Isso permite editar
 * os dados do recebedor sem que o simples salvamento dispare uma nova validação de
 * limite quando a cesta do mês já estava marcada.</p>
 */
@Service
public class RecebimentoCestaService {

    /** Quantidade máxima de meses consecutivos com cesta recebida. */
    static final int MESES_CONSECUTIVOS_MAXIMOS = 4;

    private final RecebimentoCestaRepository recebimentoCestaRepository;
    private final RecebedorRepository recebedorRepository;
    private final RecebimentoCestaMapper recebimentoCestaMapper;
    private final RecebedorCestaPdfService recebedorCestaPdfService;
    private final Clock clock;

    /**
     * Construtor com injeção de dependências.
     *
     * @param recebimentoCestaRepository repositório de recebimentos
     * @param recebedorRepository        repositório de recebedores
     * @param recebimentoCestaMapper     conversor entidade/DTO
     * @param recebedorCestaPdfService   gerador do recibo em PDF
     * @param clock                      relógio da aplicação
     */
    public RecebimentoCestaService(
            RecebimentoCestaRepository recebimentoCestaRepository,
            RecebedorRepository recebedorRepository,
            RecebimentoCestaMapper recebimentoCestaMapper,
            RecebedorCestaPdfService recebedorCestaPdfService,
            Clock clock
    ) {
        this.recebimentoCestaRepository = recebimentoCestaRepository;
        this.recebedorRepository = recebedorRepository;
        this.recebimentoCestaMapper = recebimentoCestaMapper;
        this.recebedorCestaPdfService = recebedorCestaPdfService;
        this.clock = clock;
    }

    /**
     * Verifica se o recebedor pode receber cesta no mês informado.
     *
     * @param recebedorId identificador do recebedor
     * @param referencia  mês consultado
     * @return resultado com motivo e próximo mês permitido
     * @throws EntityNotFoundException se o recebedor não existir
     */
    @Transactional(readOnly = true)
    public ElegibilidadeCestaDTO verificarElegibilidade(UUID recebedorId, YearMonth referencia) {
        Recebedor recebedor = recebedorRepository.findById(recebedorId)
                .orElseThrow(() -> new EntityNotFoundException("Recebedor not found: " + recebedorId));

        LocalDate dataReferencia = referencia.atDay(1);
        LocalDate ultimo = ultimoRecebimento(recebedorId);
        Optional<RecebimentoCesta> atual =
                recebimentoCestaRepository.findByRecebedor_IdAndDataReferencia(recebedorId, dataReferencia);

        if (atual.isPresent() && atual.get().getStatus() == StatusRecebimento.RECEBEU) {
            return new ElegibilidadeCestaDTO(
                    recebedor.getId(),
                    recebedor.getNomeCompleto(),
                    dataReferencia,
                    false,
                    0,
                    ultimo,
                    proximaDataPermitida(recebedorId, referencia.plusMonths(1)),
                    "Recebedor já recebeu a cesta neste mês"
            );
        }

        int consecutivos = contarConsecutivosAnteriores(recebedorId, referencia);
        boolean elegivel = consecutivos < MESES_CONSECUTIVOS_MAXIMOS;
        LocalDate proxima = elegivel
                ? dataReferencia
                : proximaDataPermitida(recebedorId, referencia);
        String motivo = elegivel
                ? "Elegível para receber a cesta"
                : "Limite de " + MESES_CONSECUTIVOS_MAXIMOS + " meses consecutivos atingido";

        return new ElegibilidadeCestaDTO(
                recebedor.getId(),
                recebedor.getNomeCompleto(),
                dataReferencia,
                elegivel,
                consecutivos,
                ultimo,
                proxima,
                motivo
        );
    }

    /**
     * Registra ou atualiza a oferta de cesta de um mês.
     *
     * @param recebedorId identificador do recebedor
     * @param dto         dados do recebimento
     * @return o registro salvo
     * @throws EntityNotFoundException   se o recebedor não existir
     * @throws CestaNaoPermitidaException se o limite tiver sido atingido ou o mês for futuro
     */
    @Transactional
    public RecebimentoCestaResponseDTO registrarOuAtualizar(UUID recebedorId, RecebimentoCestaCreateDTO dto) {
        if (dto == null || dto.ano() == null || dto.mes() == null || dto.status() == null) {
            throw new IllegalArgumentException("Ano, mês e status são obrigatórios");
        }

        Recebedor recebedor = recebedorRepository.findById(recebedorId)
                .orElseThrow(() -> new EntityNotFoundException("Recebedor not found: " + recebedorId));

        YearMonth referencia = YearMonth.of(dto.ano(), dto.mes().getNumero());
        if (referencia.isAfter(YearMonth.now(clock))) {
            throw new CestaNaoPermitidaException("Mês de referência não pode ser futuro");
        }

        LocalDate dataReferencia = referencia.atDay(1);
        Optional<RecebimentoCesta> existente =
                recebimentoCestaRepository.findByRecebedor_IdAndDataReferencia(recebedorId, dataReferencia);

        RecebimentoCesta recebimento = existente.orElseGet(RecebimentoCesta::new);
        StatusRecebimento anterior = recebimento.getStatus();
        boolean novo = recebimento.getId() == null;

        if (dto.status() == StatusRecebimento.RECEBEU && anterior != StatusRecebimento.RECEBEU) {
            int consecutivos = contarConsecutivosAnteriores(recebedorId, referencia);
            if (consecutivos >= MESES_CONSECUTIVOS_MAXIMOS) {
                throw new CestaNaoPermitidaException(
                        "Limite de " + MESES_CONSECUTIVOS_MAXIMOS + " meses consecutivos atingido");
            }
        }

        recebimento.setRecebedor(recebedor);
        recebimento.setDataReferencia(dataReferencia);
        recebimento.setStatus(dto.status());
        recebimento.setObservacao(dto.observacao());
        recebimento.setDataEntrega(resolverDataEntrega(dto, referencia));

        boolean atualizarSnapshot = novo
                || (dto.status() == StatusRecebimento.RECEBEU && anterior != StatusRecebimento.RECEBEU);
        if (atualizarSnapshot) {
            recebimento.setSnapshot(recebimentoCestaMapper.toSnapshot(recebedor));
        }

        RecebimentoCesta salvo = recebimentoCestaRepository.save(recebimento);
        return recebimentoCestaMapper.toResponseDTO(salvo);
    }

    /**
     * Registra o status da cesta no mês corrente.
     *
     * <p>Usado pelo formulário do recebedor. Status {@code null} significa "não
     * informado" e não cria nem altera registro.</p>
     *
     * @param recebedorId identificador do recebedor
     * @param status      status escolhido, ou {@code null}
     */
    @Transactional
    public void registrarStatusAtual(UUID recebedorId, StatusRecebimento status) {
        if (status == null) {
            return;
        }
        YearMonth agora = YearMonth.now(clock);
        registrarOuAtualizar(recebedorId, new RecebimentoCestaCreateDTO(
                agora.getYear(),
                MesRecebimento.de(agora.getMonthValue()),
                status,
                null,
                null
        ));
    }

    /**
     * Consulta o status do mês atual de um recebedor.
     *
     * @param recebedorId identificador do recebedor
     * @param referencia  mês consultado
     * @return status, ou {@code null} se não houver registro
     */
    @Transactional(readOnly = true)
    public StatusRecebimento statusAtual(UUID recebedorId, YearMonth referencia) {
        return recebimentoCestaRepository
                .findByRecebedor_IdAndDataReferencia(recebedorId, referencia.atDay(1))
                .map(RecebimentoCesta::getStatus)
                .orElse(null);
    }

    /**
     * Monta o status do mês para vários recebedores em uma única consulta.
     *
     * @param recebedorIds identificadores da página
     * @param referencia   mês consultado
     * @return mapa recebedor → status
     */
    @Transactional(readOnly = true)
    public Map<UUID, StatusRecebimento> statusDoMes(Collection<UUID> recebedorIds, YearMonth referencia) {
        if (recebedorIds == null || recebedorIds.isEmpty()) {
            return Map.of();
        }
        return recebimentoCestaRepository
                .findByDataReferenciaAndRecebedor_IdIn(referencia.atDay(1), recebedorIds)
                .stream()
                .collect(Collectors.toMap(
                        recebimento -> recebimento.getRecebedor().getId(),
                        RecebimentoCesta::getStatus,
                        (primeiro, segundo) -> segundo
                ));
    }

    /**
     * Lista o histórico de um recebedor.
     *
     * @param recebedorId identificador do recebedor
     * @return histórico do mais recente para o mais antigo
     * @throws EntityNotFoundException se o recebedor não existir
     */
    @Transactional(readOnly = true)
    public List<RecebimentoCestaResponseDTO> historico(UUID recebedorId) {
        if (!recebedorRepository.existsById(recebedorId)) {
            throw new EntityNotFoundException("Recebedor not found: " + recebedorId);
        }
        return recebimentoCestaRepository.findByRecebedor_IdOrderByDataReferenciaDesc(recebedorId)
                .stream()
                .map(recebimentoCestaMapper::toResponseDTO)
                .toList();
    }

    /**
     * Gera o PDF do recibo de um registro.
     *
     * @param recebimentoId identificador do recebimento
     * @return bytes do PDF
     * @throws EntityNotFoundException   se o recebimento não existir
     * @throws CestaNaoPermitidaException se o status não for {@code RECEBEU}
     */
    @Transactional(readOnly = true)
    public byte[] gerarReciboPdf(UUID recebimentoId) {
        RecebimentoCesta recebimento = recebimentoCestaRepository.findById(recebimentoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Recebimento de cesta not found: " + recebimentoId));
        if (recebimento.getStatus() != StatusRecebimento.RECEBEU) {
            throw new CestaNaoPermitidaException("Recibo disponível apenas para cesta recebida");
        }
        return recebedorCestaPdfService.gerar(recebimento);
    }

    /**
     * Conta quantos meses imediatamente anteriores tiveram {@code RECEBEU}.
     *
     * @param recebedorId identificador do recebedor
     * @param referencia  mês de referência
     * @return quantidade de meses consecutivos anteriores
     */
    private int contarConsecutivosAnteriores(UUID recebedorId, YearMonth referencia) {
        int consecutivos = 0;
        YearMonth cursor = referencia.minusMonths(1);
        while (consecutivos < MESES_CONSECUTIVOS_MAXIMOS) {
            Optional<RecebimentoCesta> registro = recebimentoCestaRepository
                    .findByRecebedor_IdAndDataReferencia(recebedorId, cursor.atDay(1));
            if (registro.isEmpty() || registro.get().getStatus() != StatusRecebimento.RECEBEU) {
                break;
            }
            consecutivos++;
            cursor = cursor.minusMonths(1);
        }
        return consecutivos;
    }

    /**
     * Encontra o primeiro mês, a partir do informado, em que o recebedor pode receber.
     *
     * @param recebedorId identificador do recebedor
     * @param inicio      mês inicial da busca
     * @return primeiro dia do mês permitido, ou {@code null} se não encontrado
     */
    private LocalDate proximaDataPermitida(UUID recebedorId, YearMonth inicio) {
        YearMonth cursor = inicio;
        for (int i = 0; i <= MESES_CONSECUTIVOS_MAXIMOS + 12; i++) {
            if (podeReceber(recebedorId, cursor)) {
                return cursor.atDay(1);
            }
            cursor = cursor.plusMonths(1);
        }
        return null;
    }

    /**
     * Verifica se um mês específico está liberado.
     *
     * @param recebedorId identificador do recebedor
     * @param referencia  mês consultado
     * @return {@code true} se pode receber
     */
    private boolean podeReceber(UUID recebedorId, YearMonth referencia) {
        Optional<RecebimentoCesta> atual = recebimentoCestaRepository
                .findByRecebedor_IdAndDataReferencia(recebedorId, referencia.atDay(1));
        if (atual.isPresent() && atual.get().getStatus() == StatusRecebimento.RECEBEU) {
            return false;
        }
        return contarConsecutivosAnteriores(recebedorId, referencia) < MESES_CONSECUTIVOS_MAXIMOS;
    }

    /**
     * Busca a data do último recebimento efetivo.
     *
     * @param recebedorId identificador do recebedor
     * @return data do último {@code RECEBEU}, ou {@code null}
     */
    private LocalDate ultimoRecebimento(UUID recebedorId) {
        return recebimentoCestaRepository
                .findFirstByRecebedor_IdAndStatusOrderByDataReferenciaDesc(
                        recebedorId, StatusRecebimento.RECEBEU)
                .map(RecebimentoCesta::getDataReferencia)
                .orElse(null);
    }

    /**
     * Define a data de entrega: a informada, hoje no mês corrente, ou nula.
     *
     * @param dto        dados do recebimento
     * @param referencia mês de referência
     * @return data de entrega
     */
    private LocalDate resolverDataEntrega(RecebimentoCestaCreateDTO dto, YearMonth referencia) {
        if (dto.dataEntrega() != null) {
            return dto.dataEntrega();
        }
        if (dto.status() == StatusRecebimento.RECEBEU && referencia.equals(YearMonth.now(clock))) {
            return LocalDate.now(clock);
        }
        return null;
    }
}
