package edu.fatec.poo.persistence.mysql;

import edu.fatec.poo.persistence.connection.ADaoConnection;
import edu.fatec.poo.persistence.connection.ICreateTable;

import java.sql.*;

public class mySqlCreateTable implements ICreateTable {

    private final Connection c;

    public mySqlCreateTable(ADaoConnection aDaoConnection) throws SQLException, ClassNotFoundException {
        c = aDaoConnection.getSafeConnection();
    }

    @Override
    public boolean tableExists(String nomeTabela) throws SQLException {
        DatabaseMetaData meta = c.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, nomeTabela, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    @Override
    public void createTableAll() throws SQLException {
        createTabelUsuario();
        //TODO
    }

    @Override
    public void createTabelUsuario() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS usuario (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    acesso INT NOT NULL,
                    nome VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    senha VARCHAR(100) NOT NULL,
                    cpf VARCHAR(14) UNIQUE,
                    telefone VARCHAR(15)
                );
                """;
        runStatementTabela(sql, "Usuario");
        sql = """
                INSERT INTO usuario (acesso, nome, email, senha, cpf, telefone)
                VALUES (0, 'admin', 'admin', 'admin', '12148628704', '40028922');
                """;
        runStatementData(sql, "Usuario");
    }

    private void runStatementTabela(String sql, String nomeTabela) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.execute();
            System.out.println("[MySQL] Tabela " + nomeTabela + " criada com sucesso ou já existente.");
        } catch (SQLException e) {
            System.err.println("[MySQL] Erro ao criar tabela " + nomeTabela + " no: " + e.getMessage());
            throw e;
        }
    }

    private void runStatementData(String sql, String nomeTabela) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.execute();
            System.out.println("[MySQL] Dado inserido na Tabela " + nomeTabela + " com sucesso.");
        } catch (SQLException e) {
            System.err.println("[MySQL] Erro ao inserir na tabela " + nomeTabela + " no: " + e.getMessage());
            throw e;
        }
    }
}
