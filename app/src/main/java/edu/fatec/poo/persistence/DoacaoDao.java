package edu.fatec.poo.persistence;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.persistence.connection.ADaoConnector;
import edu.fatec.poo.persistence.entityDao.GenericDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DoacaoDao extends GenericDao<Doacao> {

    public DoacaoDao(ADaoConnector connector) throws SQLException, ClassNotFoundException {
        super(connector, "doacao");
    }

    @Override
    protected Doacao map(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    protected List<Object> getAtributos(Doacao object) {
        return List.of();
    }

    @Override
    public Doacao add(Doacao object) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void update(Doacao object) throws SQLException, ClassNotFoundException {

    }
}
