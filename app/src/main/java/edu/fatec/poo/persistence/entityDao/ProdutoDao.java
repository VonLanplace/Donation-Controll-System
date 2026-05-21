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
            Long doacaoId = rs.getLong("doacao_id");
            if (!rs.wasNull()) {
                Doacao doacao = new Doacao();
                doacao.setId(doacaoId);
                produto.setDoacao(doacao);
            }
        }
        {
            Long marcaId = rs.getLong("marca_id");
            if (!rs.wasNull()) {
                MarcaProduto marca = new MarcaProduto();
                marca.setId(marcaId);
                marca.setNome(rs.getString("marca_nome"));
                produto.setMarca(marca);
            }
        }
        {
            Long tipoId = rs.getLong("tipo_id");
            if (!rs.wasNull()) {
                TipoProduto tipo = new TipoProduto();
                tipo.setId(tipoId);
                tipo.setNome(rs.getString("tipo_nome"));
                produto.setTipo(tipo);
            }
        }
        {
            Long cestaId = rs.getLong("cesta_id");
            if (!rs.wasNull()) {
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
                " (doacao_id, marca_id, tipo_id, cesta_id, codigo_barras, validade) " +
                "VALUES (?, ?, ?, ?, ?, ?);";
        List<Object> atributos = getAtributos(produto);
        runCommand(sql, atributos);
        return produto;
    }

    public List<Produto> add(List<Produto> produtos) throws SQLException, ClassNotFoundException {
        for (Produto p : produtos) {
            add(p);
        }
        return produtos;
    }

    @Override
    public void update(Produto produto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE " + tableName
                + " SET doacao_id = ?, marca_id = ?, tipo_id = ?, cesta_id = ?, " +
                "codigo_barras = ?, validade = ? WHERE id = ?;";

        List<Object> atributos = getAtributos(produto);
        atributos.add(produto.getId());
        runCommand(sql, atributos);
    }

    public void update(List<Produto> produtos) throws SQLException, ClassNotFoundException {
        for (Produto p : produtos) {
            update(p);
        }
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

    @Override
    public Produto searchById(Long id) throws SQLException, ClassNotFoundException {
        if (id == null) return null;
        StringBuilder sql = new StringBuilder();
        sql.append("""
                     SELECT
                         pro.id, pro.codigo_barras, pro.validade,
                         pro.marca_id, mar.nome AS marca_nome,
                         pro.tipo_id, tip.nome AS tipo_nome,
                         pro.doacao_id,
                         pro.cesta_id
                     FROM produto pro
                     LEFT JOIN marca_produto mar
                         ON pro.marca_id = mar.id
                     LEFT JOIN tipo_produto tip
                         ON pro.tipo_id = tip.id
                     WHERE pro.id = ?;
                """);

        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
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
        StringBuilder sql = new StringBuilder();
        sql.append("""
                    SELECT 
                        pro.id, pro.codigo_barras, pro.validade,
                        pro.marca_id, mar.nome AS marca_nome,
                        pro.tipo_id, tip.nome AS tipo_nome,
                        pro.doacao_id,
                        pro.cesta_id
                    FROM produto pro 
                    LEFT JOIN marca_produto mar
                        ON pro.marca_id = mar.id
                    LEFT JOIN tipo_produto tip
                        ON pro.tipo_id = tip.id;
                """);
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Produto> serarchAllByDoacao(Doacao doacao) throws SQLException, ClassNotFoundException {
        if (doacao == null || doacao.getId() == null) return null;
        List<Produto> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("""
                    SELECT 
                        pro.id, pro.codigo_barras, pro.validade,
                        pro.marca_id, mar.nome AS marca_nome,
                        pro.tipo_id, tip.nome AS tipo_nome,
                        pro.doacao_id,
                        pro.cesta_id
                    FROM produto pro 
                    LEFT JOIN marca_produto mar
                        ON pro.marca_id = mar.id
                    LEFT JOIN tipo_produto tip
                        ON pro.tipo_id = tip.id;
                """);
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            ps.setLong(1, doacao.getId());
            while (rs.next()) {
                Produto produto = map(rs);
                produto.setDoacao(doacao);
                list.add(produto);
            }
        }
        return list;
    }
}
