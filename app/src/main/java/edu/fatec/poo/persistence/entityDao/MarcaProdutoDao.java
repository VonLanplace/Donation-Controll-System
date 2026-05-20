package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.produto.MarcaProduto;
import edu.fatec.poo.persistence.connection.ADaoConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MarcaProdutoDao extends GenericDao<MarcaProduto> {

    public MarcaProdutoDao(ADaoConnector connector) throws SQLException, ClassNotFoundException {
        super(connector, "marca_produto");
    }

    @Override
    protected MarcaProduto map(ResultSet rs) throws SQLException {
        MarcaProduto marcaProduto = new MarcaProduto();
        marcaProduto.setId(rs.getLong("id"));
        marcaProduto.setNome(rs.getString("nome"));
        return marcaProduto;
    }

    @Override
    protected List<Object> getAtributos(MarcaProduto marcaProduto) {
        ArrayList<Object> atributos = new ArrayList<>();
        atributos.add(marcaProduto.getNome());
        return atributos;
    }

    @Override
    public MarcaProduto add(MarcaProduto marcaProduto) throws SQLException, ClassNotFoundException {
        if (marcaProduto == null) return null;
        String sql = """
                INSERT INTO marca_produto
                (nome)
                VALUES
                (?);
                """;
        List<Object> atributos = getAtributos(marcaProduto);
        try (Connection connection = conector.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (int i = 0; i < atributos.size(); i++) {
                    ps.setObject(i + 1, atributos.get(i));
                }
                ps.executeUpdate();
            }
        }
        return marcaProduto;
    }

    @Override
    public void update(MarcaProduto marcaProduto) throws SQLException, ClassNotFoundException {
        if (marcaProduto == null) return;
        String sql = """
                UPDATE marca_produto SET
                nome = ?
                WHERE
                id = ?;
                """;
        List<Object> atributos = getAtributos(marcaProduto);
        atributos.add(marcaProduto.getId());
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < atributos.size(); i++) {
                ps.setObject(i, atributos.get(i));
            }
            ps.executeUpdate();
        }
    }
}

