package edu.fatec.poo.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe abstrata que fornece a estrutura base para o gerenciamento de conexões com bancos de dados.
 * <p>
 * Implementa a interface {@link IDaoConnection} e centraliza os atributos de configuração
 * (hostname, banco de dados, usuário e senha), além de oferecer métodos para manipulação
 * segura do objeto {@link Connection}.
 * </p>
 *
 * @author Seu Nome
 * @version 1.0
 */
public abstract class ADaoConnection implements IDaoConnection {

    /**
     * Objeto de conexão ativa com o banco de dados.
     */
    protected Connection connection;

    /**
     * Endereço do servidor de banco de dados.
     */
    protected String hostname;

    /**
     * Nome do banco de dados (schema).
     */
    protected String dbName;

    /**
     * Nome de usuário para autenticação.
     */
    protected String user;

    /**
     * Senha para autenticação.
     */
    protected String senha;

    /**
     * Construtor para inicializar as configurações de acesso ao banco de dados.
     *
     * @param hostname Endereço do servidor.
     * @param dbName   Nome do banco de dados.
     * @param user     Usuário de acesso.
     * @param senha    Senha de acesso.
     */
    public ADaoConnection(String hostname, String dbName, String user, String senha) {
        this.hostname = hostname;
        this.dbName = dbName;
        this.user = user;
        this.senha = senha;
    }

    /**
     * Fecha a conexão atual com o banco de dados caso ela exista.
     *
     * @throws ClassNotFoundException Caso o driver JDBC não seja encontrado.
     * @throws SQLException           Caso ocorra um erro ao fechar a conexão.
     */
    @Override
    public void closeConnection() throws ClassNotFoundException, SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Obtém uma conexão segura. Se a conexão atual for nula, invoca o método
     * {@code getConnection()} para instanciá-la.
     *
     * @return {@link Connection} Uma conexão ativa com o banco de dados.
     * @throws SQLException           Caso ocorra erro na conexão.
     * @throws ClassNotFoundException Caso o driver JDBC não seja encontrado.
     */
    public Connection getSafeConnection() throws SQLException, ClassNotFoundException {
        if (connection == null) {
            this.connection = getConnection();
        }
        return connection;
    }

    /**
     * @param connection Define um novo objeto de conexão.
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return O hostname do servidor.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname O hostname a ser definido.
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return O nome do banco de dados.
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName O nome do banco a ser definido.
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * @return O usuário do banco de dados.
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user O usuário a ser definido.
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return A senha do banco de dados.
     */
    public String getSenha() {
        return senha;
    }

    /**
     * @param senha A senha a ser definida.
     */
    public void setSenha(String senha) {
        this.senha = senha;
    }
}