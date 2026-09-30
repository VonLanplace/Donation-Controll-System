package com.vonlanplace.doacao.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Página inicial da interface web, prefixo {@code /}.
 *
 * <p><strong>Existe porque o header apontava para cá e não havia nada.</strong> O
 * fragmento {@code fragments/header.html} tem um link para a raiz, e antes desta
 * classe a raiz respondia 404 — ou seja, todo clique no logo derrubava o usuário
 * num erro. Esta é a tela que fecha esse ciclo.</p>
 *
 * <p><strong>Índice, não painel.</strong> A lista de recursos vem do que existe, não
 * do que está previsto. Enquanto só leitura está no ar, a página mostra
 * {@code /produto} e as demais entradas ficam marcadas como indisponíveis em vez de
 * prometer link que daria 404 — que era exatamente o defeito do header antigo.</p>
 */
@Controller
public class HomeController {

    /**
     * Renderiza o índice de recursos.
     *
     * @param model modelo da view
     * @return nome da view {@code home/index}
     */
    @GetMapping({"", "/"})
    public String index(Model model) {
        model.addAttribute("titulo", "Sistema de Doação");
        return "home/index";
    }
}
