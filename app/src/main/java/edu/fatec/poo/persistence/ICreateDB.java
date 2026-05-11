package edu.fatec.poo.persistence;

import java.sql.SQLException;

/**
 * Interface que define o contrato para a criação do esquema do banco de dados.
 * <p>
 * Implementações desta interface devem conter a lógica para a execução de scripts
 * DDL necessários para inicializar tabelas e estruturas relacionais.
 * </p>
 *
 * @author Equipe de Desenvolvimento
 * @version 1.0
 */
public interface ICreateDB {

    /**
     * Cria as tabelas e a estrutura inicial do banco de dados.
     *
     * @throws SQLException Se ocorrer um erro durante a execução dos comandos SQL.
     */
    void createDatabase() throws SQLException;
}