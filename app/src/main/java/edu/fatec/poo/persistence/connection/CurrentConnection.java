package edu.fatec.poo.persistence.connection;

import edu.fatec.poo.persistence.mariaDb.mariadbDaoConnector;
import edu.fatec.poo.persistence.mariaDb.mariadbCreateDB;
import edu.fatec.poo.persistence.mariaDb.mariadbCreateTable;
import lombok.Data;

import java.sql.Connection;
import java.sql.SQLException;

@Data
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

    public void buildMariaDb(ADaoConnector connection) {
        try {
            connection = new mariadbDaoConnector(
                    "localhost",
                    "3306",
                    "sys",
                    "root",
                    "12345678"
            );
            ICreateDB createDB = new mariadbCreateDB(connection);
            createDB.createDatabase();

            connection = new mariadbDaoConnector(
                    "localhost",
                    "3306",
                    "Doacao",
                    "root",
                    "12345678"
            );
            ICreateTable createTable = new mariadbCreateTable(connection);
            createTable.createTableAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
