package com.vonlanplace.doacao.produto.controller;

import com.vonlanplace.doacao.produto.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/produto")
@Tag(name = "Produto", description = "Gerenciamento de produtos doáveis")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping({"", "/", "/listar"})
    @Operation(summary = "Listar todos os produtos cadastrados")
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoService.findAll());
        return "produto/listar";
    }
}