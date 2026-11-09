package com.vonlanplace.doacao.recebedor.cesta;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gera o recibo em PDF a partir do snapshot do recebimento.
 *
 * <p><strong>Nunca lê o cadastro atual.</strong> Todos os dados exibidos vêm de
 * {@link RecebimentoCesta#getSnapshot()}. Uma edição posterior no recebedor não
 * altera recibos já emitidos.</p>
 */
@Service
public class RecebedorCestaPdfService {

    /** Formato de data curto usado no recibo. */
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Formato de mês/ano usado no recibo. */
    private static final DateTimeFormatter MES_ANO = DateTimeFormatter.ofPattern("MM/yyyy");

    /** Formato de data e hora da emissão. */
    private static final DateTimeFormatter EMISSAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Gera o PDF do recibo.
     *
     * @param recebimento registro com snapshot preenchido
     * @return bytes do PDF
     * @throws CestaNaoPermitidaException se o registro não tiver snapshot
     * @throws IllegalStateException      se o PDF não puder ser montado
     */
    public byte[] gerar(RecebimentoCesta recebimento) {
        SnapshotRecebedor snapshot = recebimento.getSnapshot();
        if (snapshot == null) {
            throw new CestaNaoPermitidaException("Recibo sem snapshot de recebedor");
        }

        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(documento, saida);
            documento.open();

            Font titulo = fonte(FontFactory.HELVETICA_BOLD, 16);
            Font secao = fonte(FontFactory.HELVETICA_BOLD, 11);
            Font normal = fonte(FontFactory.HELVETICA, 10);
            Font pequena = fonte(FontFactory.HELVETICA, 8);

            Paragraph cabecalho = new Paragraph("Recibo de Cesta Básica", titulo);
            cabecalho.setSpacingAfter(4);
            documento.add(cabecalho);
            documento.add(new Paragraph("Recibo nº " + recebimento.getId(), normal));
            documento.add(new Paragraph(" "));

            documento.add(secao("Recebimento", secao));
            PdfPTable recebimentoTabela = tabela();
            linha(recebimentoTabela, "Mês de referência",
                    MES_ANO.format(recebimento.getDataReferencia()), normal);
            linha(recebimentoTabela, "Status", recebimento.getStatus().getDescricao(), normal);
            linha(recebimentoTabela, "Data de entrega",
                    recebimento.getDataEntrega() == null ? "—" : DATA.format(recebimento.getDataEntrega()), normal);
            linha(recebimentoTabela, "Observação",
                    texto(recebimento.getObservacao()), normal);
            documento.add(recebimentoTabela);

            documento.add(secao("Dados do recebedor", secao));
            PdfPTable recebedorTabela = tabela();
            linha(recebedorTabela, "Nome completo", texto(snapshot.getNomeCompleto()), normal);
            linha(recebedorTabela, "CPF", texto(snapshot.getCpf()), normal);
            linha(recebedorTabela, "RG", texto(snapshot.getRgNum()), normal);
            linha(recebedorTabela, "Órgão emissor",
                    snapshot.getRgEmissor() == null ? "—" : snapshot.getRgEmissor().getDescricao(), normal);
            linha(recebedorTabela, "Data de nascimento",
                    snapshot.getDataNascimento() == null ? "—" : DATA.format(snapshot.getDataNascimento()), normal);
            linha(recebedorTabela, "Telefone pessoal", texto(snapshot.getTelefonePessoal()), normal);
            linha(recebedorTabela, "Telefone de recado", texto(snapshot.getTelefoneRecado()), normal);
            linha(recebedorTabela, "E-mail", texto(snapshot.getEmail()), normal);
            linha(recebedorTabela, "PIX", texto(snapshot.getPix()), normal);
            linha(recebedorTabela, "Lê e escreve", simNao(snapshot.isLeEscreve()), normal);
            linha(recebedorTabela, "Acompanhamento jurídico", simNao(snapshot.isNecessitaJuridico()), normal);
            linha(recebedorTabela, "Acompanhamento psicológico", simNao(snapshot.isNecessitaPsicologo()), normal);
            linha(recebedorTabela, "Parecer do entrevistador", simNao(snapshot.isParecerEntrevistador()), normal);
            linha(recebedorTabela, "Comentários", texto(snapshot.getComentarios()), normal);
            documento.add(recebedorTabela);

            documento.add(secao("Endereço", secao));
            PdfPTable enderecoTabela = tabela();
            linha(enderecoTabela, "Logradouro", texto(snapshot.getLogradouro()), normal);
            linha(enderecoTabela, "Número", texto(snapshot.getNumero()), normal);
            linha(enderecoTabela, "Complemento", texto(snapshot.getComplemento()), normal);
            linha(enderecoTabela, "Bairro", texto(snapshot.getBairro()), normal);
            linha(enderecoTabela, "Cidade", texto(snapshot.getCidade()), normal);
            linha(enderecoTabela, "CEP", texto(snapshot.getCep()), normal);
            documento.add(enderecoTabela);

            documento.add(new Paragraph(" "));
            documento.add(new Paragraph(
                    "Documento gerado a partir do snapshot do cadastro no momento do recebimento.",
                    pequena));
            documento.add(new Paragraph("Emitido em " + EMISSAO.format(LocalDateTime.now()), pequena));
            documento.add(new Paragraph(" "));
            documento.add(new Paragraph("________________________________________________", normal));
            documento.add(new Paragraph("Assinatura do responsável pela entrega", pequena));
        } catch (DocumentException exception) {
            throw new IllegalStateException("Falha ao gerar recibo em PDF", exception);
        } finally {
            if (documento.isOpen()) {
                documento.close();
            }
        }
        return saida.toByteArray();
    }

    /**
     * Monta uma fonte com a codificação WinAnsi, que cobre os acentos do português.
     *
     * @param nome nome da fonte
     * @param tamanho tamanho em pontos
     * @return fonte configurada
     */
    private Font fonte(String nome, float tamanho) {
        return FontFactory.getFont(nome, BaseFont.WINANSI, tamanho);
    }

    /**
     * Cria um parágrafo de seção com espaçamento.
     *
     * @param texto texto da seção
     * @param fonte fonte da seção
     * @return parágrafo pronto
     */
    private Paragraph secao(String texto, Font fonte) {
        Paragraph paragrafo = new Paragraph(texto, fonte);
        paragrafo.setSpacingBefore(10);
        paragrafo.setSpacingAfter(4);
        return paragrafo;
    }

    /**
     * Cria uma tabela de duas colunas com largura total.
     *
     * @return tabela configurada
     * @throws DocumentException se os pesos das colunas forem inválidos
     */
    private PdfPTable tabela() throws DocumentException {
        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[]{1.4f, 3.6f});
        return tabela;
    }

    /**
     * Adiciona uma linha rótulo/valor à tabela.
     *
     * @param tabela tabela de destino
     * @param rotulo rótulo da linha
     * @param valor  valor da linha
     * @param fonte  fonte do valor
     */
    private void linha(PdfPTable tabela, String rotulo, String valor, Font fonte) {
        PdfPCell celulaRotulo = new PdfPCell(new Phrase(rotulo, fonte));
        PdfPCell celulaValor = new PdfPCell(new Phrase(texto(valor), fonte));
        celulaRotulo.setPadding(4);
        celulaValor.setPadding(4);
        tabela.addCell(celulaRotulo);
        tabela.addCell(celulaValor);
    }

    /**
     * Normaliza texto nulo ou em branco para travessão.
     *
     * @param valor texto original
     * @return texto exibível
     */
    private String texto(String valor) {
        return valor == null || valor.isBlank() ? "—" : valor;
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
}
