package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.persistence.connection.ADaoConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProdutoDao extends GenericDao<Produto> {
    public ProdutoDao(ADaoConnector aDaoConnector) throws SQLException, ClassNotFoundException {
        super(aDaoConnector, "produto");
    }

    @Override
    protected Produto map(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    protected List<Object> getAtributos(Produto object) {
        return List.of();
    }

    @Override
    public Produto add(Produto object) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void update(Produto object) throws SQLException, ClassNotFoundException {

    }
}
