package edu.fatec.poo.persistence.entityDao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface genérica para o padrão de projeto Data Access Object (DAO).
 * Define as operações básicas de CRUD (Create, Read, Update, Delete) para persistência de dados.
 *
 * @param <T> O tipo da entidade que será manipulada por esta interface.
 * @author Seu Nome
 * @version 1.1
 */
public interface IDao<T> {

    /**
     * Persiste um novo objeto no banco de dados.
     *
     * @param object O objeto contendo os dados a serem inseridos.
     * @return
     * @throws SQLException Se ocorrer um erro durante a execução da query SQL.
     */
    public T add(T object) throws SQLException, ClassNotFoundException;

    /**
     * Realiza a busca de um objeto específico com base em atributos preenchidos.
     * Geralmente utiliza um atributo identificador presente no objeto de exemplo.
     *
     * @param object O objeto contendo os critérios de busca (ex: objeto com ID setado).
     * @return O objeto preenchido com os dados do banco, ou {@code null} se não encontrado.
     * @throws SQLException Se ocorrer um erro durante o acesso ao banco de dados.
     */
    public T search(T object) throws SQLException, ClassNotFoundException;

    /**
     * Localiza um registro através do seu identificador único (Primary Key).
     *
     * @param id O identificador único do registro no banco de dados.
     * @return O objeto correspondente ao ID informado, ou {@code null} se não encontrado.
     * @throws SQLException Se ocorrer um erro durante a execução da busca.
     */
    public T searchById(Long id) throws SQLException, ClassNotFoundException;

    /**
     * Atualiza os dados de um registro existente no banco de dados.
     *
     * @param object O objeto contendo o ID do registro e os novos valores.
     * @throws SQLException Se ocorrer um erro durante a atualização dos dados.
     */
    public void update(T object) throws SQLException, ClassNotFoundException;

    /**
     * Remove um registro do banco de dados de forma permanente.
     *
     * @param object O objeto (ou critério) que identifica o registro a ser excluído.
     * @throws SQLException Se ocorrer um erro durante a exclusão.
     */
    public void delete(T object) throws SQLException, ClassNotFoundException;

    /**
     * Recupera todos os registros da entidade armazenados na tabela correspondente.
     *
     * @return Uma {@link List} contendo todos os registros encontrados,
     * ou uma lista vazia caso a tabela esteja vazia.
     * @throws SQLException Se ocorrer um erro durante a recuperação dos dados.
     */
    public List<T> searchAll() throws SQLException, ClassNotFoundException;
}