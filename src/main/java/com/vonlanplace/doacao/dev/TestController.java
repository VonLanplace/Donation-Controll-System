package com.vonlanplace.doacao.dev;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller de teste, prefixo {@code /test}.
 *
 * <p><strong>É andaime, não funcionalidade.</strong> Não fala com nenhum repositório nem
 * serviço: apenas devolve a view {@code test}, que existe em
 * {@code src/main/resources/templates/test.html}. Serve para confirmar que o Tomcat
 * subiu e que o MVC resolveu view e controller, e nada além disso.</p>
 *
 * <p><strong>Mora no pacote {@code dev} para ficar fora do pacote raiz.</strong> O
 * pacote {@code com.vonlanplace.doacao} é a fonte de Component Scanning e guarda só o
 * ponto de entrada, {@code DoacaoApplication}. Tudo que não pertence a um feature
 * —configuração global, tratamento de exceção, andaime de verificação— tem subpacote
 * próprio, e este é o do andaime.</p>
 *
 * <p><strong>Pode ser removido à vontade.</strong> Nada do projeto depende desta rota, e
 * ela não faz parte de nenhum fluxo de domínio. Enquanto o equivalente Thymeleaf de
 * produto ({@code ProdutoViewController}, prefixo {@code /produto}) está quebrado por
 * apontar para uma view inexistente, este é o único caminho web que renderiza.</p>
 */
@Controller
@RequestMapping("/test")
public class TestController {

    /**
     * Renderiza a view de teste.
     *
     * @return nome da view {@code test}
     */
    @GetMapping({"", "/"})
    @Operation(summary = "Render the test view")
    public String renderizarTeste() {
        return "test";
    }
}
