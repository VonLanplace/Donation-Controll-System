package edu.fatec.poo.persistence.connection;

import java.sql.Connection;
import java.sql.SQLException;

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

    public String getPorta() {
        return porta;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}