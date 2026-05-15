package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.connection.ADaoConnector;
import edu.fatec.poo.util.Acesso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao extends GenericDao<Usuario> implements IDao<Usuario> {

    public UsuarioDao(ADaoConnector aDaoConnector) throws SQLException, ClassNotFoundException {
        super(aDaoConnector, "usuario");
    }

    @Override
    public Usuario add(Usuario usuario) throws SQLException, ClassNotFoundException {
        //TODO
        String sql = """
                INSERT INTO usuario
                (acesso, nome, email, senha, cpf, telefone)
                VALUES
                (?,?,?,?,?,?);
                """;
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, usuario.getAcesso().getIndice());
            ps.setString(2, usuario.getNome());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getSenha());
            ps.setString(5, usuario.getCpf());
            ps.setString(6, String.valueOf(usuario.getTelefone()));

            ps.execute();
        }
        return usuario;
    }

    public Usuario searchByEmail(String email) throws SQLException, ClassNotFoundException {
        return searchByField("email", email);
    }

    public Usuario searchByCpf(String cpf) throws SQLException, ClassNotFoundException {
        return searchByField("cpf", cpf);
    }

    private Usuario searchByField(String fieldName, Object valor) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM usuario WHERE " + fieldName + " = ?;";
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void update(Usuario usuario) throws SQLException, ClassNotFoundException {
        String sql = """
                UPDATE usuario SET
                acesso = ?, 
                nome = ?, 
                email = ?, 
                senha = ?,
                cpf = ?,
                telefone = ?
                WHERE id = ?;
                """;
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            List<Object> parametros = getAtributos(usuario);
            parametros.add(usuario.getId());
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            ps.execute();
        }
    }

    private List<Usuario> searchAllSortedByName() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM usuario ORDER BY nome ASC;";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection connection = conector.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(map(rs));
            }
        }
        return usuarios;
    }

    public Usuario map(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(resultSet.getLong("id"));
        usuario.setAcesso(Acesso.getAcesso(resultSet.getInt("acesso")));
        usuario.setNome(resultSet.getString("nome"));
        usuario.setEmail(resultSet.getString("email"));
        usuario.setSenha(resultSet.getString("senha"));
        usuario.setCpf(resultSet.getString("cpf"));
        usuario.setTelefone(Long.parseLong(resultSet.getString("telefone")));
        return usuario;
    }

    @Override
    protected ArrayList<Object> getAtributos(Usuario usuario) {
        ArrayList<Object> atributos = new ArrayList<>();
        atributos.add(usuario.getAcesso().getIndice());
        atributos.add(usuario.getNome());
        atributos.add(usuario.getEmail());
        atributos.add(usuario.getSenha());
        atributos.add(usuario.getCpf());
        atributos.add(usuario.getTelefone());
        return atributos;
    }

}
