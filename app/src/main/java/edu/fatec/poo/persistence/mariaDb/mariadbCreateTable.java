package edu.fatec.poo.persistence.mariaDb;

import edu.fatec.poo.persistence.connection.ADaoConnector;
import edu.fatec.poo.persistence.connection.ICreateTable;

import java.sql.*;

public class mariadbCreateTable implements ICreateTable {

    private final Connection connection;

    public mariadbCreateTable(Connection connection) throws SQLException, ClassNotFoundException {
        this.connection = connection;
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
                SELECT 0, 'admin', 'admin', 'admin', '12148628704', '40028922'
                WHERE NOT EXISTS (
                    SELECT 1 FROM usuario WHERE acesso = 0
                );
                """;
        runStatementData(sql, "Usuario");
    }

    private void runStatementTabela(String sql, String nomeTabela) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            System.out.println(ps.executeUpdate());
            System.out.println("[Mariadb] Tabela " + nomeTabela + " criada com sucesso ou já existente.");
        } catch (SQLException e) {
            System.err.println("[Mariadb] Erro ao criar tabela " + nomeTabela + " no: " + e.getMessage());
            throw e;
        }
    }

    private void runStatementData(String sql, String nomeTabela) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            System.out.println(ps.executeUpdate());
            System.out.println("[Mariadb] Dado inserido na Tabela " + nomeTabela + " com sucesso.");
        } catch (SQLException e) {
            System.err.println("[Mariadb] Erro ao inserir na tabela " + nomeTabela + " no: " + e.getMessage());
            throw e;
        }
    }
}
