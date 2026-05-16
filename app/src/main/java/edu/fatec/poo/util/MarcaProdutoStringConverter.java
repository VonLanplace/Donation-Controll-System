package edu.fatec.poo.util;

import edu.fatec.poo.model.produto.MarcaProduto;
import javafx.util.StringConverter;

public class MarcaProdutoStringConverter extends StringConverter<MarcaProduto> {
    @Override
    public String toString(MarcaProduto marcaProduto) {
        if (marcaProduto == null) return null;
        return marcaProduto.getNome() != null ? marcaProduto.getNome() : null;
    }

    @Override
    public MarcaProduto fromString(String string) {
        return null; // TODO
    }
}
