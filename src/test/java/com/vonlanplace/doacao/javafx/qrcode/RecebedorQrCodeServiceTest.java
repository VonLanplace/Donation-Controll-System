package com.vonlanplace.doacao.javafx.qrcode;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes do serviço que monta a URL e gera o QR code do cadastro de recebedor.
 */
@DisplayName("RecebedorQrCodeService")
class RecebedorQrCodeServiceTest {

    @Test
    @DisplayName("usa a URL base configurada e remove a barra final")
    void usaUrlBaseConfigurada() {
        RecebedorQrCodeService service =
                new RecebedorQrCodeService("http://192.168.0.10:8080/", null);

        assertThat(service.resolverUrlCadastro())
                .isEqualTo("http://192.168.0.10:8080/recebedor/create");
    }

    @Test
    @DisplayName("gera um PNG de QR code que aponta para a URL de cadastro")
    void geraQrCodeComUrlDeCadastro() throws IOException, NotFoundException {
        RecebedorQrCodeService service =
                new RecebedorQrCodeService("http://192.168.0.10:8080", null);

        RecebedorQrCodeDTO dados = service.gerar();

        assertThat(dados.url()).isEqualTo("http://192.168.0.10:8080/recebedor/create");
        assertThat(dados.png()).isNotEmpty();
        assertThat(dados.png()[0]).isEqualTo((byte) 0x89);

        BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(dados.png()));
        Result resultado = new MultiFormatReader().decode(
                new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(imagem)))
        );
        assertThat(resultado.getText()).isEqualTo(dados.url());
    }

    @Test
    @DisplayName("sem URL base, monta uma URL HTTP local para o cadastro")
    void semUrlBaseMontaUrlLocal() {
        RecebedorQrCodeService service = new RecebedorQrCodeService("", null);

        assertThat(service.resolverUrlCadastro())
                .startsWith("http://")
                .endsWith("/recebedor/create");
    }
}
