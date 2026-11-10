package com.vonlanplace.doacao.javafx.qrcode;

/**
 * Conteúdo exibido na tela JavaFX de cadastro de recebedor.
 *
 * @param url endereço web do formulário de cadastro
 * @param png imagem PNG do QR code que aponta para {@code url}
 */
public record RecebedorQrCodeDTO(String url, byte[] png) {
}
