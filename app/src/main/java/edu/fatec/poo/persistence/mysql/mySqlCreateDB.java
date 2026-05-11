package edu.fatec.poo.persistence.mysql;

import edu.fatec.poo.persistence.ADaoConnection;
import edu.fatec.poo.persistence.ICreateDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class mySqlCreateDB implements ICreateDB {

    private final Connection c;

    public mySqlCreateDB(ADaoConnection aDaoConnection) throws SQLException, ClassNotFoundException {
        c = aDaoConnection.getSafeConnection();
    }

    @Override
    public void createDatabase() throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS store_cakes " +
                "CHARACTER SET utf8mb4 " +
                "COLLATE utf8mb4_unicode_ci";

        try (Statement st = c.createStatement()) {
            st.executeUpdate(sql);
            System.out.println("[MySQL] Banco criado com sucesso ou já existente.");
        } catch (SQLException e) {
            System.err.println("[MySQL] Erro ao criar banco no: " + e.getMessage());
            throw e;
        }
    }
}
