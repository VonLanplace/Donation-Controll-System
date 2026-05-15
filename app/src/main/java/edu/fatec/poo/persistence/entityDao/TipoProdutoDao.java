package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.produto.TipoProduto;
import edu.fatec.poo.persistence.connection.ADaoConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoProdutoDao extends GenericDao<TipoProduto> {
    public TipoProdutoDao(ADaoConnector connector) throws SQLException, ClassNotFoundException {
        super(connector, "tipo_produto");
    }

    @Override
    protected TipoProduto map(ResultSet rs) throws SQLException {
        TipoProduto tipoProduto = new TipoProduto();
        tipoProduto.setId(rs.getLong("id"));
        tipoProduto.setNome(rs.getString("nome"));
        return tipoProduto;
    }

    @Override
    protected List<Object> getAtributos(TipoProduto tipoProduto) {
        ArrayList<Object> atributos = new ArrayList<>();
        atributos.add(tipoProduto.getNome());
        return atributos;
    }

    @Override
    public TipoProduto add(TipoProduto tipoProduto) throws SQLException, ClassNotFoundException {
        if (tipoProduto == null) return null;
        String sql = """
                INSERT INTO tipo_produto
                (nome)
                VALUES
                (?);
                """;
        List<Object> atributos = getAtributos(tipoProduto);
        try (Connection connection = conector.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.executeUpdate();
                for (int i = 0; i < atributos.size(); i++) {
                    ps.setObject(i + 1, atributos.get(i));
                }
            }
        }
        return tipoProduto;
    }

    @Override
    public void update(TipoProduto tipoProduto) throws SQLException, ClassNotFoundException {
        if (tipoProduto == null) return;
        String sql = """
                UPDATE tipo_produto SET
                nome = ?
                WHERE
                id = ?;
                """;
        List<Object> atributos = getAtributos(tipoProduto);
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < atributos.size(); i++) {
                ps.setObject(i, atributos.get(i));
            }
            ps.executeUpdate();
        }
    }
}
