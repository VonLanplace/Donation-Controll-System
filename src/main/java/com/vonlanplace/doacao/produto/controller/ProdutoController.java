package com.vonlanplace.doacao.produto.controller;

import com.vonlanplace.doacao.produto.Produto;
import com.vonlanplace.doacao.produto.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/produto")
@Tag(name = "Produto", description = "Gerenciamento de produtos doáveis")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping({"", "/", "/listar"})
    @Operation(summary = "List all Products")
    public String listarProdutos(
            @PageableDefault(size = 20, sort = "codigoBarras") Pageable pageable,
            Model model
    ) {
        model.addAttribute("produtos", produtoService.findAll(pageable));
        return "produto/listar";
    }

    @GetMapping("/create")
    @Operation(summary = "Create a Product")
    public String createProduto(
            Model model
    ) {
        model.addAttribute("produto", new Produto());
        return "produto/form";
    }

    @GetMapping("/update")
    @Operation(summary = "Update a Product")
    public String updateProduto(
            Model model,
            @RequestParam UUID id
    ) {
        model.addAttribute("produto", produtoService.findById(id));
        return "produto/form";
    }

}