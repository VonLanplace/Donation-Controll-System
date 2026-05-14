package edu.fatec.poo.persistence.connection;

import java.sql.SQLException;

public interface ICreateTable {

    public void createTableAll() throws SQLException;

    public void createTabelUsuario() throws SQLException;
}
