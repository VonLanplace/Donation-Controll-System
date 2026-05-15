package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.IEntity;
import edu.fatec.poo.persistence.connection.ADaoConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericDao<T extends IEntity> implements IDao<T> {

    protected ADaoConnector conector;
    protected String tableName;

    public GenericDao(ADaoConnector aDaoConnector, String tableName) throws SQLException, ClassNotFoundException {
        this.conector = aDaoConnector;
        this.tableName = tableName;
    }

    protected abstract T map(ResultSet rs) throws SQLException;

    protected abstract List<Object> getAtributos(T object);

    @Override
    public T search(T object) throws SQLException, ClassNotFoundException {
        return searchById(object.getId());
    }

    @Override
    public T searchById(Long id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<T> searchAll() throws SQLException, ClassNotFoundException {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public void delete(T object) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?;";
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, object.getId());
            ps.execute();
        }
    }
}