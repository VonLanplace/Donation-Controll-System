package edu.fatec.poo.persistence.mysql;

import edu.fatec.poo.persistence.ADaoConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mysqlIDaoConnection extends ADaoConnection {

    public mysqlIDaoConnection(String hostname, String dbName, String user, String senha) {
        super(hostname, dbName, user, senha);
    }

    @Override
    public Connection getConnection()
            throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        String url = String.format(
                "jdbc:mysql://%s:3306/%s?allowPublicKeyRetrieval=true&useTimezone=true&serverTimezone=UTC&useSSL=false",
                hostname, dbName
        );

        connection = DriverManager.getConnection(url, user, senha);

        return connection;
    }

}
