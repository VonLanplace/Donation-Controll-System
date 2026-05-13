package edu.fatec.poo.persistence;

import edu.fatec.poo.persistence.mysql.mySqlCreateDB;
import edu.fatec.poo.persistence.mysql.mySqlCreateTable;
import edu.fatec.poo.persistence.mysql.mysqlIDaoConnection;
import lombok.Data;

@Data
public class CurrentConnection {
    public static ADaoConnection connection;
    private static ICreateDB createDB;
    private static ICreateTable createTable;

    public CurrentConnection() {
        try {
            connection = new mysqlIDaoConnection(
                    "localhost",
                    "master",
                    "root",
                    "qwer@1234"
            );
            mySqlCreateDB createDB = new mySqlCreateDB(connection);
            createDB.createDatabase();
            connection.closeConnection();

            connection = new mysqlIDaoConnection(
                    "localhost",
                    "Doacao",
                    "root",
                    "qwer@1234"
            );
            createTable = new mySqlCreateTable(connection);
            createTable.createTableAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
