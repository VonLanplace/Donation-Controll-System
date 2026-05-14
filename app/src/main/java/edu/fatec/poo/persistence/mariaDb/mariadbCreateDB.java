package edu.fatec.poo.persistence.mariaDb;

import edu.fatec.poo.persistence.connection.ADaoConnector;
import edu.fatec.poo.persistence.connection.ICreateDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class mariadbCreateDB implements ICreateDB {

    private final Connection c;

    public mariadbCreateDB(ADaoConnector aDaoConnector) throws SQLException, ClassNotFoundException {
        c = aDaoConnector.getConnection();
    }

    @Override
    public void createDatabase() throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS Doacao";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
            c.close();
            System.out.println("[Mariadb] Banco criado com sucesso ou já existente.");
        } catch (SQLException e) {
            System.err.println("[Mariadb] Erro ao criar banco no: " + e.getMessage());
            throw e;
        }
    }
}
