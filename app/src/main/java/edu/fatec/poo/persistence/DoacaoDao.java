package edu.fatec.poo.persistence;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.persistence.connection.ADaoConnector;
import edu.fatec.poo.persistence.entityDao.GenericDao;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DoacaoDao extends GenericDao<Doacao> {

    public DoacaoDao(ADaoConnector connector) throws SQLException, ClassNotFoundException {
        super(connector, "doacao");
    }

    @Override
    protected Doacao map(ResultSet rs) throws SQLException {
        System.out.println("MAPED");
        return null;
    }

    @Override
    protected List<Object> getAtributos(Doacao object) {
        System.out.println("ATRIBUIDO");
        return List.of();
    }

    @Override
    public Doacao add(Doacao object) throws SQLException, ClassNotFoundException {
        System.out.println("SAVED");
        return null;
    }

    @Override
    public void update(Doacao object) throws SQLException, ClassNotFoundException {
        System.out.println("UPDATED");
    }

    public List<Doacao> searchLastNByDate(int n) {
        System.out.println("Found Last date " + n);
        return List.of();
    }

    public List<Doacao> searchByLikeName(String s) {
        System.out.println("Found By Name Like " + s);
        return List.of();
    }
}
