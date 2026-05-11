package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.IEntity;
import edu.fatec.poo.persistence.ADaoConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata que fornece uma implementação base para operações de acesso a dados (DAO)
 * utilizando Generics. Esta classe implementa o padrão Data Access Object para entidades
 * que estendem {@link IEntity}.
 *
 * @param <T> O tipo da entidade que estende {@link IEntity}.
 * @author Equipe de Desenvolvimento
 * @version 1.0
 */
public abstract class GenericDao<T extends IEntity> implements IDao<T> {

    /**
     * Conexão ativa com o banco de dados.
     */
    protected Connection connection;

    /**
     * Nome da tabela associada à entidade no banco de dados.
     */
    protected String tableName;

    /**
     * Construtor que inicializa a conexão e define o nome da tabela.
     *
     * @param aDaoConnection Objeto de conexão que fornece a instância de {@link Connection}.
     * @param tableName      Nome da tabela no banco de dados.
     * @throws SQLException           Se ocorrer um erro ao obter a conexão.
     * @throws ClassNotFoundException Se o driver do banco de dados não for encontrado.
     */
    public GenericDao(ADaoConnection aDaoConnection, String tableName) throws SQLException, ClassNotFoundException {
        this.connection = aDaoConnection.getSafeConnection();
        this.tableName = tableName;
    }

    /**
     * Converte um registro do {@link ResultSet} em um objeto do tipo {@code T}.
     * Este método deve ser implementado pelas subclasses para mapear as colunas da tabela.
     *
     * @param rs O ResultSet posicionado no registro atual.
     * @return Uma instância da entidade preenchida com os dados do banco.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    protected abstract T map(ResultSet rs) throws SQLException;

    /**
     * Extrai os atributos do objeto para uma lista de parâmetros SQL.
     * Útil para operações de inserção e atualização.
     *
     * @param object A instância da entidade.
     * @return Lista de objetos contendo os valores dos campos.
     */
    protected abstract List<Object> getParameters(T object);

    /**
     * Realiza a busca de uma entidade utilizando seu identificador único.
     *
     * @param object O objeto contendo o ID a ser pesquisado.
     * @return O objeto preenchido encontrado ou {@code null} se não existir.
     * @throws SQLException Se ocorrer um erro na execução da consulta SQL.
     */
    @Override
    public T search(T object) throws SQLException {
        return searchById(object.getId());
    }

    /**
     * Busca um registro na tabela pelo ID.
     *
     * @param id O identificador único do registro.
     * @return A entidade correspondente ou {@code null} se nenhum registro for encontrado.
     * @throws SQLException Se ocorrer um erro de banco de dados.
     */
    @Override
    public T searchById(Long id) throws SQLException {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /**
     * Recupera todos os registros da tabela associada.
     *
     * @return Uma lista contendo todas as instâncias encontradas.
     * @throws SQLException Se ocorrer um erro durante a execução do comando SQL.
     */
    @Override
    public List<T> searchAll() throws SQLException {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /**
     * Remove um registro do banco de dados baseado no ID do objeto fornecido.
     *
     * @param object O objeto a ser removido.
     * @throws SQLException Se ocorrer um erro durante a deleção.
     */
    @Override
    public void delete(T object) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, object.getId());
            ps.execute();
        }
    }
}