package edu.fatec.poo.persistence.mysql;

import edu.fatec.poo.persistence.ADaoConnection;
import edu.fatec.poo.persistence.ICreateTable;

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
        createTabelCliente();
        //TODO
    }

    @Override
    public void createTabelCliente() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS cliente (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    telefone INT,
                    endereco_logradouro VARCHAR(150),
                    endereco_cep VARCHAR(9),
                    endereco_num INT,
                    endereco_complemento VARCHAR(50)
                );
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.execute();
            System.out.println("[MySQL] Tabela Cliente criada com sucesso ou já existente.");
        } catch (SQLException e) {
            System.err.println("[MySQL] Erro ao criar tabela Cliente no: " + e.getMessage());
            throw e;
        }
    }
}
