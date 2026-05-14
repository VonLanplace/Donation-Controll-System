package edu.fatec.poo.persistence.connection;

import lombok.Data;

import java.sql.Connection;
import java.sql.SQLException;

@Data
public abstract class ADaoConnector {

    protected String porta;
    protected String hostname;
    protected String dbName;
    protected String user;
    protected String senha;

    public ADaoConnector(String hostname, String porta, String dbName, String user, String senha) {
        this.hostname = hostname;
        this.porta = porta;
        this.dbName = dbName;
        this.user = user;
        this.senha = senha;
    }

    public abstract Connection getConnection() throws ClassNotFoundException, SQLException;
}