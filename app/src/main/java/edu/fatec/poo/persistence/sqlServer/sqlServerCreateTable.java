package edu.fatec.poo.persistence.sqlServer;

import edu.fatec.poo.persistence.ADaoConnection;
import edu.fatec.poo.persistence.ICreateTable;

import java.sql.*;

public class sqlServerCreateTable implements ICreateTable {

    private final Connection c;

    public sqlServerCreateTable(ADaoConnection aDaoConnection) throws SQLException, ClassNotFoundException {
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
                IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[cliente]') AND type in (N'U'))
                BEGIN
                    CREATE TABLE cliente (
                        id INT IDENTITY(1,1) PRIMARY KEY,
                        nome VARCHAR(100) NOT NULL,
                        email VARCHAR(100) UNIQUE NOT NULL,
                        telefone VARCHAR(20),
                        endereco_logradouro VARCHAR(150),
                        endereco_cep VARCHAR(9),
                        endereco_num INT,
                        endereco_complemento VARCHAR(50)
                    );
                END
                """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.execute();
            System.out.println("[SQL Server] Tabela Cliente criada com sucesso ou já existente.");
        } catch (SQLException e) {
            System.err.println("[SQL Server] Erro ao criar tabela Cliente no: " + e.getMessage());
            throw e;
        }
    }
}
