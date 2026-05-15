package edu.fatec.poo.persistence.connection;

import java.sql.SQLException;

public interface ICreateTable {

    void createTableAll() throws SQLException;

    void createTabelUsuario() throws SQLException;

    void createTabelTipoProduto() throws SQLException;

    void createTableMarcaProduto() throws SQLException;
}
