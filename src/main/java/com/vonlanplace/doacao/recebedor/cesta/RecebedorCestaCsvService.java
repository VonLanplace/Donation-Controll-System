package com.vonlanplace.doacao.recebedor.cesta;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Exporta os recebimentos de cesta em CSV.
 *
 * <p><strong>Uma linha por recebedor.</strong> Cada mês do intervalo vira um grupo de
 * colunas com o snapshot completo daquele mês. Meses sem recebimento ficam em branco.
 * Só entram recebedores com ao menos um {@code RECEBEU} no período.</p>
 *
 * <p><strong>O CSV é largo de propósito.</strong> A escolha foi "snapshot completo por
 * mês"; o limite de 60 meses evita um arquivo com milhares de colunas.</p>
 */
@Service
public class RecebedorCestaCsvService {

    /** Limite de meses por exportação. */
    static final int MAX_MESES_EXPORTACAO = 60;

    /** Formato de data usado nos campos. */
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Campos do snapshot repetidos em cada mês. */
    private static final List<String> CAMPOS_MES = List.of(
            "status",
            "data_entrega",
            "observacao",
            "nome_completo",
            "cpf",
            "rg_num",
            "rg_emissor",
            "data_nascimento",
            "telefone_pessoal",
            "telefone_recado",
            "le_escreve",
            "email",
            "pix",
            "necessita_juridico",
            "necessita_psicologo",
            "parecer_entrevistador",
            "comentarios",
            "logradouro",
            "numero",
            "complemento",
            "bairro",
            "cidade",
            "cep"
    );

    private final RecebimentoCestaRepository recebimentoCestaRepository;

    /**
     * Construtor com injeção de dependência.
     *
     * @param recebimentoCestaRepository repositório de recebimentos
     */
    public RecebedorCestaCsvService(RecebimentoCestaRepository recebimentoCestaRepository) {
        this.recebimentoCestaRepository = recebimentoCestaRepository;
    }

    /**
     * Gera o CSV do período.
     *
     * @param inicio mês inicial, inclusive
     * @param fim    mês final, inclusive
     * @return bytes do CSV em UTF-8 com BOM
     * @throws IllegalArgumentException se o período for nulo, invertido ou maior que 60 meses
     */
    @Transactional(readOnly = true)
    public byte[] exportar(YearMonth inicio, YearMonth fim) {
        validarPeriodo(inicio, fim);

        LocalDate primeiroDia = inicio.atDay(1);
        LocalDate ultimoDia = fim.atEndOfMonth();
        List<RecebimentoCesta> recebimentos =
                recebimentoCestaRepository.findByStatusAndDataReferenciaBetweenOrderByRecebedor_IdAscDataReferenciaAsc(
                        StatusRecebimento.RECEBEU, primeiroDia, ultimoDia);

        Map<UUID, List<RecebimentoCesta>> porRecebedor = recebimentos.stream()
                .collect(Collectors.groupingBy(
                        recebimento -> recebimento.getRecebedor().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<RelatorioRecebedor> linhas = porRecebedor.values().stream()
                .map(RelatorioRecebedor::new)
                .sorted(Comparator.comparing(
                        (RelatorioRecebedor linha) -> linha.snapshot().getNomeCompleto(),
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();

        CsvWriter csv = new CsvWriter();
        csv.linha(cabecalho(inicio, fim));
        for (RelatorioRecebedor linha : linhas) {
            csv.linha(valores(linha, inicio, fim));
        }
        return csv.toBytes();
    }

    /**
     * Valida o intervalo solicitado.
     *
     * @param inicio mês inicial
     * @param fim    mês final
     * @throws IllegalArgumentException se o intervalo for inválido
     */
    private void validarPeriodo(YearMonth inicio, YearMonth fim) {
        if (inicio == null || fim == null) {
            throw new IllegalArgumentException("Informe o mês inicial e o mês final.");
        }
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("O mês inicial deve ser anterior ou igual ao mês final.");
        }
        long meses = ChronoUnit.MONTHS.between(inicio, fim) + 1;
        if (meses > MAX_MESES_EXPORTACAO) {
            throw new IllegalArgumentException("O período máximo é de " + MAX_MESES_EXPORTACAO + " meses.");
        }
    }

    /**
     * Monta o cabeçalho fixo e os grupos mensais.
     *
     * @param inicio mês inicial
     * @param fim    mês final
     * @return lista de colunas
     */
    private List<String> cabecalho(YearMonth inicio, YearMonth fim) {
        List<String> colunas = new ArrayList<>(List.of(
                "recebedor_id",
                "nome_completo",
                "cpf",
                "total_recebimentos",
                "ultimo_mes_recebido"
        ));
        for (YearMonth mes = inicio; !mes.isAfter(fim); mes = mes.plusMonths(1)) {
            for (String campo : CAMPOS_MES) {
                colunas.add(mes + "_" + campo);
            }
        }
        return colunas;
    }

    /**
     * Monta os valores de uma linha.
     *
     * @param linha  recebedor e seus recebimentos
     * @param inicio mês inicial
     * @param fim    mês final
     * @return lista de valores na mesma ordem do cabeçalho
     */
    private List<String> valores(RelatorioRecebedor linha, YearMonth inicio, YearMonth fim) {
        SnapshotRecebedor snapshot = linha.snapshot();
        List<String> valores = new ArrayList<>(List.of(
                linha.recebedorId().toString(),
                texto(snapshot == null ? null : snapshot.getNomeCompleto()),
                texto(snapshot == null ? null : snapshot.getCpf()),
                String.valueOf(linha.recebimentos().size()),
                linha.ultimoMes().toString()
        ));
        Map<YearMonth, RecebimentoCesta> porMes = linha.porMes();
        for (YearMonth mes = inicio; !mes.isAfter(fim); mes = mes.plusMonths(1)) {
            RecebimentoCesta recebimento = porMes.get(mes);
            for (String campo : CAMPOS_MES) {
                valores.add(valorCampo(recebimento, campo));
            }
        }
        return valores;
    }

    /**
     * Extrai um campo de um recebimento.
     *
     * @param recebimento recebimento do mês, ou {@code null}
     * @param campo       nome do campo
     * @return valor formatado
     */
    private String valorCampo(RecebimentoCesta recebimento, String campo) {
        if (recebimento == null) {
            return "";
        }
        SnapshotRecebedor snapshot = recebimento.getSnapshot();
        if (snapshot == null) {
            return switch (campo) {
                case "status" -> recebimento.getStatus().getDescricao();
                case "data_entrega" -> formatar(recebimento.getDataEntrega());
                case "observacao" -> texto(recebimento.getObservacao());
                default -> "";
            };
        }
        return switch (campo) {
            case "status" -> recebimento.getStatus().getDescricao();
            case "data_entrega" -> formatar(recebimento.getDataEntrega());
            case "observacao" -> texto(recebimento.getObservacao());
            case "nome_completo" -> texto(snapshot.getNomeCompleto());
            case "cpf" -> texto(snapshot.getCpf());
            case "rg_num" -> texto(snapshot.getRgNum());
            case "rg_emissor" -> snapshot.getRgEmissor() == null
                    ? "" : snapshot.getRgEmissor().getDescricao();
            case "data_nascimento" -> formatar(snapshot.getDataNascimento());
            case "telefone_pessoal" -> texto(snapshot.getTelefonePessoal());
            case "telefone_recado" -> texto(snapshot.getTelefoneRecado());
            case "le_escreve" -> simNao(snapshot.isLeEscreve());
            case "email" -> texto(snapshot.getEmail());
            case "pix" -> texto(snapshot.getPix());
            case "necessita_juridico" -> simNao(snapshot.isNecessitaJuridico());
            case "necessita_psicologo" -> simNao(snapshot.isNecessitaPsicologo());
            case "parecer_entrevistador" -> simNao(snapshot.isParecerEntrevistador());
            case "comentarios" -> texto(snapshot.getComentarios());
            case "logradouro" -> texto(snapshot.getLogradouro());
            case "numero" -> texto(snapshot.getNumero());
            case "complemento" -> texto(snapshot.getComplemento());
            case "bairro" -> texto(snapshot.getBairro());
            case "cidade" -> texto(snapshot.getCidade());
            case "cep" -> texto(snapshot.getCep());
            default -> "";
        };
    }

    /**
     * Formata uma data ou devolve vazio.
     *
     * @param data data original
     * @return data formatada ou vazio
     */
    private String formatar(LocalDate data) {
        return data == null ? "" : DATA.format(data);
    }

    /**
     * Normaliza nulo ou branco para vazio.
     *
     * @param valor texto original
     * @return texto exibível
     */
    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    /**
     * Converte booleano em Sim/Não.
     *
     * @param valor booleano
     * @return texto exibível
     */
    private String simNao(boolean valor) {
        return valor ? "Sim" : "Não";
    }

    /**
     * Agrupamento de um recebedor e seus recebimentos no período.
     *
     * @param recebedorId identificador do recebedor
     * @param recebimentos recebimentos em ordem crescente de mês
     */
    private record RelatorioRecebedor(UUID recebedorId, List<RecebimentoCesta> recebimentos) {

        /**
         * Cria o agrupamento a partir da lista de recebimentos.
         *
         * @param recebimentos recebimentos do mesmo recebedor
         */
        RelatorioRecebedor(List<RecebimentoCesta> recebimentos) {
            this(recebimentos.get(0).getRecebedor().getId(), recebimentos);
        }

        /**
         * Snapshot do recebimento mais recente.
         *
         * @return snapshot mais recente
         */
        SnapshotRecebedor snapshot() {
            return recebimentos.get(recebimentos.size() - 1).getSnapshot();
        }

        /**
         * Último mês com recebimento.
         *
         * @return último mês do período
         */
        YearMonth ultimoMes() {
            return YearMonth.from(recebimentos.get(recebimentos.size() - 1).getDataReferencia());
        }

        /**
         * Indexa os recebimentos por mês.
         *
         * @return mapa mês para recebimento
         */
        Map<YearMonth, RecebimentoCesta> porMes() {
            return recebimentos.stream().collect(Collectors.toMap(
                    recebimento -> YearMonth.from(recebimento.getDataReferencia()),
                    recebimento -> recebimento,
                    (primeiro, segundo) -> primeiro,
                    LinkedHashMap::new));
        }
    }
}
