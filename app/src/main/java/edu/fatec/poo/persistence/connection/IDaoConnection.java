package edu.fatec.poo.persistence.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDaoConnection {
    public Connection getConnection() throws ClassNotFoundException, SQLException;

    public void closeConnection() throws ClassNotFoundException, SQLException;
}
