package edu.fatec.poo.persistence.mariaDb;

import edu.fatec.poo.persistence.connection.ADaoConnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mariadbDaoConnector extends ADaoConnector {

    public mariadbDaoConnector(String hostname, String porta, String dbName, String user, String senha) {
        super(hostname, porta, dbName, user, senha);
    }

    @Override
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");

        String url = String.format(
                "jdbc:mariadb://%s:%s/%s?allowPublicKeyRetrieval=true&useSSL=false",
                hostname, porta, dbName
        );

        return DriverManager.getConnection(url, user, senha);
    }
}
