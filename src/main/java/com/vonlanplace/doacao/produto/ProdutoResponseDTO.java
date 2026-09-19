package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.cesta.Cesta;
import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoResponseDTO;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoResponseDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProdutoResponseDTO(
        UUID id,
        String codigoBarras,
        MarcaProdutoResponseDTO marcaProduto,
        TipoProdutoResponseDTO tipoProduto
) {
}
