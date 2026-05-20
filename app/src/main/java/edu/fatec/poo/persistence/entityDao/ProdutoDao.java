package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.Cesta;
import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.produto.MarcaProduto;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.model.produto.TipoProduto;
import edu.fatec.poo.persistence.connection.ADaoConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDao extends GenericDao<Produto> {
    public ProdutoDao(ADaoConnector aDaoConnector) throws SQLException, ClassNotFoundException {
        super(aDaoConnector, "produto");
    }

    @Override
    protected Produto map(ResultSet rs) throws SQLException, ClassNotFoundException {
        Produto produto = new Produto();
        produto.setId(rs.getLong("id"));
        {
            Long doacaoId = rs.getLong("id_doacao");
            if (!rs.wasNull()) {
                Doacao doacao = new Doacao();
                doacao.setId(doacaoId);
                produto.setDoacao(doacao);
            }
        }
        {
            Long marcaId = rs.getLong("id_marca");
            if (!rs.wasNull()) {
                MarcaProdutoDao marcaDao = new MarcaProdutoDao(conector);
                produto.setMarca(marcaDao.searchById(marcaId));
            }
        }
        {
            Long tipoId = rs.getLong("id_tipo");
            if (!rs.wasNull()) {
                TipoProdutoDao tipoDao = new TipoProdutoDao(conector);
                produto.setTipo(tipoDao.searchById(tipoId));
            }
        }
        {
            Long cestaId = rs.getLong("id_cesta");
            if (!rs.wasNull()) {
                // TODO - IMPLEMT CESTA ON SYSTEM
                Cesta cesta = new Cesta();
                cesta.setId(cestaId);
                produto.setCesta(cesta);
            }
        }
        produto.setCodigoBarras(rs.getString("codigo_barras"));
        produto.setDataValidade(rs.getDate("validade").toLocalDate());
        return produto;
    }

    @Override
    protected List<Object> getAtributos(Produto produto) {
        ArrayList<Object> atributos = new ArrayList<>();
        atributos.add(produto.getDoacao() == null ? null : produto.getDoacao().getId());
        atributos.add(produto.getMarca() == null ? null : produto.getMarca().getId());
        atributos.add(produto.getTipo() == null ? null : produto.getTipo().getId());
        atributos.add(produto.getCesta() == null ? null : produto.getCesta().getId());
        atributos.add(produto.getCodigoBarras());
        atributos.add(produto.getDataValidade());
        return atributos;
    }

    @Override
    public Produto add(Produto produto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO " + tableName +
                " (id_doacao, id_marca, id_tipo, id_cesta, codigo_barras, validade) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
        List<Object> atributos = getAtributos(produto);
        runCommand(sql, atributos);
        return produto;
    }

    @Override
    public void update(Produto produto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE " + tableName
                + " SET id_doacao = ?, id_marca = ?, id_tipo = ?, id_cesta = ?, " +
                "codigo_barras = ?, validade = ? WHERE id = ?;";

        List<Object> atributos = getAtributos(produto);
        atributos.add(produto.getId());
        runCommand(sql, atributos);
    }

    private void runCommand(String sql, List<Object> atributos) throws SQLException, ClassNotFoundException {
        try (Connection connection = conector.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                for (int i = 0; i < atributos.size(); i++) {
                    ps.setObject(i + 1, atributos.get(i));
                }
                ps.executeUpdate();
            }
        }
    }
asdasd
    @Override
    public Produto searchById(Long id) throws SQLException, ClassNotFoundException {
        if (id == null) return null;
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Produto> searchAll() throws SQLException, ClassNotFoundException {
        List<Produto> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }
}
