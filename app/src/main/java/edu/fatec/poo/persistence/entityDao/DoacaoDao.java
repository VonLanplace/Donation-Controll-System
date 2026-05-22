package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.connection.ADaoConnector;
import edu.fatec.poo.util.Acesso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DoacaoDao {
    protected ADaoConnector conector;
    protected String tableName;

    public DoacaoDao(ADaoConnector connector) throws SQLException, ClassNotFoundException {
        this.conector = connector;
        this.tableName = "doacao";
    }

    protected Doacao map(ResultSet rs) throws SQLException {
        Doacao doacao = new Doacao();
        doacao.setId(rs.getString("id"));
        doacao.setNomeDoador(rs.getString("nome_doador"));
        doacao.setData(rs.getDate("data").toLocalDate());

        {
            Long usuarioId = rs.getLong("usuario_id");
            if (!rs.wasNull()) {
                Usuario cadastrante = new Usuario();
                cadastrante.setId(usuarioId);
                cadastrante.setAcesso(Acesso.getAcesso(rs.getInt("usuario_acesso")));
                cadastrante.setNome(rs.getString("usuario_nome"));
                cadastrante.setEmail(rs.getString("usuario_email"));
                cadastrante.setSenha(rs.getString("usuario_senha"));
                cadastrante.setCpf(rs.getString("usuario_cpf"));
                cadastrante.setTelefone(rs.getLong("usuario_telefone"));
                doacao.setCadastrante(cadastrante);
            }
        }
        return doacao;
    }

    protected List<Object> getAtributos(Doacao doacao) {
        List<Object> atributos = new ArrayList<>();
        atributos.add(doacao.getNomeDoador());
        atributos.add(doacao.getData());
        atributos.add(doacao.getCadastrante() != null
                && doacao.getCadastrante().getId() != null
                ? doacao.getCadastrante().getId() : null);
        return atributos;
    }

    public Doacao add(Doacao doacao) throws SQLException, ClassNotFoundException {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName);
        sql.append("(id, nome_doador, data, usuario_id)");
        sql.append(" VALUES ");
        sql.append("(?, ?, ?, ?)");

        List<Object> atributos = new ArrayList<>();
        atributos.add(doacao.getId());
        atributos.addAll(getAtributos(doacao));
        runCommand(sql.toString(), atributos);
        
        return doacao;
    }

    public void update(Doacao doacao) throws SQLException, ClassNotFoundException {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ").append(tableName);
        sql.append(" SET ");
        sql.append("nome_doador = ?,");
        sql.append("data = ?,");
        sql.append("usuario_id = ?");
        sql.append(" WHERE ");
        sql.append("id = ?");
        List<Object> atributos = getAtributos(doacao);
        atributos.add(doacao.getId());
        runCommand(sql.toString(), atributos);
    }

    public List<Doacao> searchLastNByDate(int n) throws SQLException, ClassNotFoundException {
        List<Doacao> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("doa.id, doa.nome_doador, doa.data, doa.usuario_id, ");
        sql.append("usu.acesso AS usuario_acesso, ");
        sql.append("usu.nome AS usuario_nome, usu.email AS usuario_email, ");
        sql.append("usu.senha AS usuario_senha, ");
        sql.append("usu.cpf AS usuario_cpf, usu.telefone AS usuario_telefone ");
        sql.append("FROM doacao doa ");
        sql.append("LEFT JOIN usuario usu ON doa.usuario_id = usu.id ");
        sql.append("ORDER BY doa.data DESC ");
        sql.append("LIMIT ?");

        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setInt(1, n);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Doacao> searchByName(String s) throws SQLException, ClassNotFoundException {
        System.out.println("Found By Name Like " + s);
        return List.of();
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

    public Doacao searchById(UUID id) throws SQLException, ClassNotFoundException {
        if (id == null) return null;

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("doa.id, doa.nome_doador, doa.data, doa.usuario_id, ");
        sql.append("usu.acesso AS usuario_acesso, ");
        sql.append("usu.nome AS usuario_nome, usu.email AS usuario_email, ");
        sql.append("usu.senha AS usuario_senha, usu.cpf AS usuario_cpf, ");
        sql.append("usu.telefone AS usuario_telefone ");
        sql.append("FROM doacao doa ");
        sql.append("LEFT JOIN usuario usu ");
        sql.append("ON doa.usuario_id = usu.id ");
        sql.append("WHERE doa.id = ?;");

        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())
        ) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        }
        return null;
    }

    public List<Doacao> searchAll() throws SQLException, ClassNotFoundException {
        List<Doacao> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT");
        sql.append("doa.id, doa.nome_doador, doa.data, doa.usuario_id, ");
        sql.append("usu.acesso AS usuario_acesso, ");
        sql.append("usu.nome AS usuario_nome, usu.email AS usuario_email, ");
        sql.append("usu.senha AS usuario_senha, usu.cpf AS usuario_cpf, ");
        sql.append("usu.cpf AS usuario_cpf, usu.telefone AS usuario_telefone ");
        sql.append("FROM doacao doa ");
        sql.append("LEFT JOIN usuario usu ");
        sql.append("ON doa.usuario_id = usu.id ");

        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public void delete(Doacao doacao) throws SQLException, ClassNotFoundException {
        if (doacao == null) return;
        if (doacao.getId() == null) return;
        String sql = "DELETE FROM doacao WHERE id = ?;";
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, doacao.getId().toString());
            ps.execute();
        }
    }
}
