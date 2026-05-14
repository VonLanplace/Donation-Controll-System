package edu.fatec.poo.persistence.connection;

import java.sql.SQLException;

public interface ICreateTable {
    public boolean tableExists(String nomeTabela) throws SQLException;

    public void createTableAll() throws SQLException;

    public void createTabelUsuario() throws SQLException;
}
