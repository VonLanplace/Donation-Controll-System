package edu.fatec.poo.persistence.connection;

import edu.fatec.poo.persistence.mariaDb.mariadbCreateDB;
import edu.fatec.poo.persistence.mariaDb.mariadbCreateTable;
import edu.fatec.poo.persistence.mariaDb.mariadbDaoConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class CurrentConnection {

    private ADaoConnector conector;

    public CurrentConnection() {
        conector = new mariadbDaoConnector(
                "localhost",
                "3306",
                "Doacao",
                "root",
                "12345678"
        );
    }

    private Connection connect() throws SQLException, ClassNotFoundException {
        return conector.getConnection();
    }

    public void buildMariaDb() {
        ADaoConnector connector;
        try {
            connector = new mariadbDaoConnector(
                    "localhost",
                    "3306",
                    "sys",
                    "root",
                    "12345678"
            );
            try (Connection connection = connector.getConnection()) {
                ICreateDB createDB = new mariadbCreateDB(connection);
                createDB.createDatabase();
            }

            connector = new mariadbDaoConnector(
                    "localhost",
                    "3306",
                    "Doacao",
                    "root",
                    "12345678"
            );
            try (Connection connection = connector.getConnection()) {
                ICreateTable createTable = new mariadbCreateTable(connection);
                createTable.createTableAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ADaoConnector getConector() {
        return conector;
    }

    public void setConector(ADaoConnector conector) {
        this.conector = conector;
    }

    @Override
    public String toString() {
        return "CurrentConnection{" +
                "conector=" + conector +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CurrentConnection that = (CurrentConnection) o;
        return Objects.equals(conector, that.conector);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(conector);
    }
}
