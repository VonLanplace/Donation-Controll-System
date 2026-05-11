package edu.fatec.poo.persistence.sqlServer;

import edu.fatec.poo.persistence.ADaoConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class sqlServerIDaoConnection extends ADaoConnection {

    public sqlServerIDaoConnection(String hostname, String dbName, String user, String senha) {
        super(hostname, dbName, user, senha);
    }

    @Override
    public Connection getConnection()
            throws ClassNotFoundException, SQLException {
        Class.forName("net.sourceforge.jtds.jdbc.Driver");

        connection = DriverManager.getConnection(
                String.format(
                        "jdbc:jtds:sqlserver://%s:1433;databaseName=%s;user=%s;password=%s",
                        hostname, dbName, user, senha
                )
        );

        return connection;
    }
}
