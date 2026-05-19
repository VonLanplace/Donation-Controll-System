package edu.fatec.poo.util;

import edu.fatec.poo.model.produto.TipoProduto;
import javafx.util.StringConverter;

public class TipoProdutoStringConverter extends StringConverter<TipoProduto> {
    @Override
    public String toString(TipoProduto tipoProduto) {
        if (tipoProduto == null) return null;
        return tipoProduto.getNome() != null ? tipoProduto.getNome() : null;
    }

    @Override
    public TipoProduto fromString(String string) {
        return null; // TODO
    }
}
