package com.vonlanplace.doacao.javafx.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Map;

/**
 * Monta a URL web de cadastro e gera o QR code exibido no JavaFX.
 *
 * <p><strong>O QR code aponta para a aplicação web, não para uma tela JavaFX.</strong>
 * O formulário de recebedor tem abas e tabelas dinâmicas; no celular ele é preenchido
 * pelo navegador, que já é a fonte de verdade dos dados. A janela desktop serve como
 * ponto de entrada: mostra o link e o código para escanear.</p>
 *
 * <p><strong>IP local automático.</strong> Sem {@code app.web.base-url}, o serviço
 * procura um IPv4 ativo e site-local. Isso evita gravar {@code localhost} no QR code,
 * que só funcionaria no próprio computador. Em redes com VPN, Docker ou várias
 * interfaces, o override em {@code application.properties} tem prioridade.</p>
 */
@Slf4j
@Service
public class RecebedorQrCodeService {

    /** Caminho do formulário web de cadastro de recebedor. */
    private static final String CAMINHO_CADASTRO = "/recebedor/create";

    /** Lado da imagem quadrada do QR code, em pixels. */
    private static final int TAMANHO_QR_CODE = 360;

    /** Porta usada quando o servidor web ainda não expõe a porta real. */
    private static final int PORTA_PADRAO = 8080;

    /** URL base configurada manualmente; vazia dispara a detecção automática. */
    private final String baseUrlConfigurada;

    /** Contexto da aplicação, usado para descobrir a porta real do Tomcat. */
    private final ApplicationContext applicationContext;

    /**
     * Construtor com injeção de dependências.
     *
     * @param baseUrlConfigurada URL base opcional definida em {@code app.web.base-url}
     * @param applicationContext contexto da aplicação
     */
    public RecebedorQrCodeService(
            @Value("${app.web.base-url:}") String baseUrlConfigurada,
            ApplicationContext applicationContext
    ) {
        this.baseUrlConfigurada = baseUrlConfigurada;
        this.applicationContext = applicationContext;
    }

    /**
     * Gera a URL de cadastro e a imagem PNG do QR code correspondente.
     *
     * @return URL e bytes PNG do QR code
     */
    public RecebedorQrCodeDTO gerar() {
        String url = resolverUrlCadastro();
        return new RecebedorQrCodeDTO(url, gerarPng(url));
    }

    /**
     * Resolve a URL que será codificada no QR code.
     *
     * @return URL absoluta do formulário web de cadastro
     */
    public String resolverUrlCadastro() {
        String baseUrl = StringUtils.hasText(baseUrlConfigurada)
                ? removerBarraFinal(baseUrlConfigurada.trim())
                : "http://" + detectarIpLocal() + ":" + resolverPorta();
        return baseUrl + CAMINHO_CADASTRO;
    }

    /**
     * Gera um PNG em memória a partir de um texto.
     *
     * @param conteudo texto que será codificado
     * @return bytes da imagem PNG
     * @throws IllegalStateException se o QR code não puder ser gerado
     */
    public byte[] gerarPng(String conteudo) {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            BitMatrix matrix = new QRCodeWriter().encode(
                    conteudo,
                    BarcodeFormat.QR_CODE,
                    TAMANHO_QR_CODE,
                    TAMANHO_QR_CODE,
                    hints
            );
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", saida);
            return saida.toByteArray();
        } catch (WriterException | IOException ex) {
            throw new IllegalStateException("Não foi possível gerar o QR code do cadastro.", ex);
        }
    }

    /**
     * Descobre a porta real do servidor web.
     *
     * @return porta HTTP em uso, ou {@value #PORTA_PADRAO} quando indisponível
     */
    private int resolverPorta() {
        try {
            if (applicationContext instanceof WebServerApplicationContext webContext
                    && webContext.getWebServer() != null) {
                int porta = webContext.getWebServer().getPort();
                if (porta > 0) {
                    return porta;
                }
            }
        } catch (IllegalStateException ex) {
            log.debug("Servidor web ainda não iniciado ao resolver a porta do QR code", ex);
        }
        return PORTA_PADRAO;
    }

    /**
     * Procura um IPv4 local adequado para ser acessado por outro dispositivo.
     *
     * <p>Prefere endereços site-local (faixas privadas) e ignora loopback,
     * link-local e interfaces virtuais. Se nada for encontrado, usa
     * {@code localhost}, que mantém a tela funcional no próprio computador.</p>
     *
     * @return IPv4 local ou {@code localhost}
     */
    private String detectarIpLocal() {
        String candidato = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface rede = interfaces.nextElement();
                if (!rede.isUp() || rede.isLoopback() || rede.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> enderecos = rede.getInetAddresses();
                while (enderecos.hasMoreElements()) {
                    InetAddress endereco = enderecos.nextElement();
                    if (!(endereco instanceof Inet4Address)
                            || endereco.isLoopbackAddress()
                            || endereco.isLinkLocalAddress()) {
                        continue;
                    }
                    if (endereco.isSiteLocalAddress()) {
                        return endereco.getHostAddress();
                    }
                    if (candidato == null) {
                        candidato = endereco.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            log.warn("Não foi possível listar as interfaces de rede para o QR code", ex);
        }
        return candidato != null ? candidato : "localhost";
    }

    /**
     * Remove barras finais para evitar URL com barra duplicada.
     *
     * @param url URL base informada
     * @return URL sem barras no final
     */
    private String removerBarraFinal(String url) {
        String resultado = url;
        while (resultado.endsWith("/")) {
            resultado = resultado.substring(0, resultado.length() - 1);
        }
        return resultado;
    }
}
