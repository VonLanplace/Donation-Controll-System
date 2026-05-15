package edu.fatec.poo.adapter.produto;

import edu.fatec.poo.model.produto.MarcaProduto;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.model.produto.TipoProduto;

import java.time.LocalDate;

public record DtoProdutoCadastrarDoacao(
        MarcaProduto marca, TipoProduto tipo,
        String codigo, LocalDate validade
) {
    public DtoProdutoCadastrarDoacao {
        if (marca == null)
            throw new IllegalArgumentException("Marca inválido ou não selecionado");

        if (tipo == null)
            throw new IllegalArgumentException("Tipo inválido ou não selecionado.");

        if (codigo != null && !codigo.isBlank() && codigo.length() > 13)
            throw new IllegalArgumentException("Código maior que o limite de Código de Barras");

        if (validade.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Produto com data de validade já passada!!");
    }

    public Produto toProduto() {
        Produto produto = new Produto();
        produto.setCesta(null);
        produto.setMarca(marca);
        produto.setTipo(tipo);
        produto.setCodigoBarras(codigo);
        produto.setDataValidade(validade);
        return produto;
    }
}
