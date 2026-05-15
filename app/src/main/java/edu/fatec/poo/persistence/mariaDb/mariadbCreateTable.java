package edu.fatec.poo.persistence.mariaDb;

import edu.fatec.poo.persistence.connection.ICreateTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class mariadbCreateTable implements ICreateTable {

    private final Connection connection;

    public mariadbCreateTable(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createTableAll() throws SQLException {
        createTabelUsuario();
        createTabelTipoProduto();
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
        sql = """
                INSERT INTO usuario (acesso, nome, email, senha, cpf, telefone)
                SELECT 1, 'user', 'user', 'user', '09876543210', '40028922'
                WHERE NOT EXISTS (
                    SELECT 1 FROM usuario WHERE acesso = 1
                );
                """;
        runStatementData(sql, "Usuario");
    }

    @Override
    public void createTabelTipoProduto() throws SQLException {
        String nomeTabela = "tipo_produto";
        String sql = """
                CREATE TABLE IF NOT EXISTS tipo_produto(
                    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                );
                """;
        runStatementTabela(sql, nomeTabela);
        String slq = """
                INSERT INTO tipo_produto (nome)
                SELECT * FROM (
                    SELECT 'Arroz' AS nome UNION ALL
                    SELECT 'Feijão' UNION ALL
                    SELECT 'Macarrão' UNION ALL
                    SELECT 'Sabão em barra' UNION ALL
                    SELECT 'Detergente' UNION ALL
                    SELECT 'Papel higiênico' UNION ALL
                    SELECT 'Milho' UNION ALL
                    SELECT 'Sardinha' UNION ALL
                    SELECT 'Ervilha' UNION ALL
                    SELECT 'Óleo de soja' UNION ALL
                    SELECT 'Margarina' UNION ALL
                    SELECT 'Leite em pó' UNION ALL
                    SELECT 'Leite longa vida'
                ) AS novos_valores
                WHERE NOT EXISTS (SELECT id FROM tipo_produto)
                """;
        runStatementData(sql, nomeTabela);
    }

    private void runStatementTabela(String sql, String nomeTabela) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            System.out.println("[Mariadb] Tabela " + nomeTabela + " criada com sucesso ou já existente.");
        } catch (SQLException e) {
            System.err.println("[Mariadb] Erro ao criar tabela " + nomeTabela + " no: " + e.getMessage());
            throw e;
        }
    }

    private void runStatementData(String sql, String nomeTabela) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
            System.out.println("[Mariadb] Dado inserido na Tabela " + nomeTabela + " com sucesso.");
        } catch (SQLException e) {
            System.err.println("[Mariadb] Erro ao inserir na tabela " + nomeTabela + " no: " + e.getMessage());
            throw e;
        }
    }
}
