package edu.fatec.poo.persistence;

import java.sql.SQLException;

public interface ICreateTable {
    public boolean tableExists(String nomeTabela) throws SQLException;

    public void createTableAll() throws SQLException;

    public void createTabelCliente() throws SQLException;
}
