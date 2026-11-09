package com.vonlanplace.doacao.javafx.controller;

import com.vonlanplace.doacao.javafx.qrcode.RecebedorQrCodeDTO;
import com.vonlanplace.doacao.javafx.qrcode.RecebedorQrCodeService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Controller da tela JavaFX que apresenta o cadastro web de recebedor.
 *
 * <p><strong>A tela não duplica o formulário.</strong> O cadastro tem abas e
 * tabelas dinâmicas no navegador; aqui o objetivo é entregar o link por QR code
 * para o celular e oferecer um atalho para abrir no navegador do computador.</p>
 *
 * <p>O controller é um bean do Spring, resolvido pelo {@code FXMLLoader} através
 * do {@code StageManager}, e recebe o serviço de QR code por construtor.</p>
 */
@Slf4j
@Component
public class RecebedorQrCodeController {

    /** Serviço que resolve a URL e gera o PNG do QR code. */
    private final RecebedorQrCodeService qrCodeService;

    /** Imagem do QR code exibida na tela. */
    @FXML
    private ImageView qrCodeImage;

    /** Link clicável para o formulário web. */
    @FXML
    private Hyperlink linkCadastro;

    /** Mensagem de sucesso ou de erro exibida abaixo do link. */
    @FXML
    private Label statusLabel;

    /** URL atualmente exibida, usada pelo botão de abrir no navegador. */
    private String urlAtual;

    /**
     * Construtor package-private: a instância vem do Spring, não do {@code FXMLLoader}.
     *
     * @param qrCodeService serviço de geração do QR code
     */
    RecebedorQrCodeController(RecebedorQrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    /**
     * Carrega a URL e o QR code quando o FXML é montado.
     */
    @FXML
    public void initialize() {
        atualizarQrCode();
    }

    /**
     * Regenera o QR code e a URL exibida.
     *
     * <p>Útil quando o IP da máquina muda ou quando o cadastro web é reiniciado
     * em outra porta.</p>
     */
    @FXML
    public void atualizarQrCode() {
        try {
            RecebedorQrCodeDTO dados = qrCodeService.gerar();
            urlAtual = dados.url();
            qrCodeImage.setImage(new Image(new ByteArrayInputStream(dados.png())));
            linkCadastro.setText(dados.url());
            statusLabel.setText("Escaneie o QR code com a câmera do celular.");
            log.info("QR code de cadastro apontando para {}", dados.url());
        } catch (RuntimeException ex) {
            log.error("Falha ao gerar o QR code do cadastro de recebedor", ex);
            statusLabel.setText("Não foi possível gerar o QR code. Tente atualizar.");
        }
    }

    /**
     * Abre a URL do cadastro no navegador padrão.
     *
     * @param actionEvent evento do clique, não utilizado
     */
    @FXML
    public void abrirNoNavegador(ActionEvent actionEvent) {
        if (urlAtual == null || urlAtual.isBlank()) {
            statusLabel.setText("A URL do cadastro ainda não foi carregada.");
            return;
        }
        if (!Desktop.isDesktopSupported()
                || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            statusLabel.setText("Este ambiente não permite abrir o navegador automaticamente.");
            return;
        }

        try {
            Desktop.getDesktop().browse(new URI(urlAtual));
        } catch (IOException | URISyntaxException ex) {
            log.warn("Não foi possível abrir o navegador para {}", urlAtual, ex);
            statusLabel.setText("Não foi possível abrir o navegador.");
        }
    }
}
