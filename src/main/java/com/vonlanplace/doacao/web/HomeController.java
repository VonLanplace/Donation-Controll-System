package com.vonlanplace.doacao.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Ponto de entrada da interface web.
 *
 * <p><strong>Redireciona a raiz para a listagem de recebedores.</strong> O logo do
 * header e o link da página de erro apontam para {@code /}; sem esta rota a raiz
 * responderia 404. Não há painel nem índice: o único recurso web é o cadastro de
 * recebedores e seus filhos.</p>
 */
@Controller
public class HomeController {

    /**
     * Redireciona a raiz para a listagem de recebedores.
     *
     * @return redirect para {@code /recebedor}
     */
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/recebedor";
    }
}
