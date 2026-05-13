package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.ADaoConnection;
import edu.fatec.poo.util.Acesso;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao extends GenericDao<Usuario> implements IDao<Usuario> {

    public UsuarioDao(ADaoConnection aDaoConnection) throws SQLException, ClassNotFoundException {
        super(aDaoConnection, "usuario");
    }

    @Override
    public Usuario add(Usuario usuario) throws SQLException {
        //TODO
        String sql = """
                INSERT INTO usuario
                (acesso, nome, email, senha, cpf, telefone)
                VALUES
                (?,?,?,?,?,?);
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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

    public Usuario searchByEmail(String email) throws SQLException {
        return searchByField("email", email);
    }

    public Usuario searchByNome(String nome) throws SQLException {
        return searchByField("nome", nome);
    }

    private Usuario searchByField(String fieldName, Object valor) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE " + fieldName + " = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
    public void update(Usuario usuario) throws SQLException {
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

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            List<Object> parametros = getParameters(usuario);
            parametros.add(usuario.getId());
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            ps.execute();
        }
    }

    private List<Usuario> searchAllSortedByName() throws SQLException {
        String sql = "SELECT * FROM usuario ORDER BY nome ASC;";
        List<Usuario> usuarios = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(map(rs));
            }
        }
        return usuarios;
    }

    public Usuario map(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(resultSet.getInt("id"));
        usuario.setAcesso(Acesso.getAcesso(resultSet.getInt("acesso")));
        usuario.setNome(resultSet.getString("nome"));
        usuario.setEmail(resultSet.getString("email"));
        usuario.setSenha(resultSet.getString("senha"));
        usuario.setCpf(resultSet.getString("cpf"));
        usuario.setTelefone(Long.parseLong(resultSet.getString("telefone")));
        return usuario;
    }

    @Override
    protected ArrayList<Object> getParameters(Usuario usuario) {
        ArrayList<Object> parametros = new ArrayList<>();
        parametros.add(usuario.getAcesso().getIndice());
        parametros.add(usuario.getNome());
        parametros.add(usuario.getEmail());
        parametros.add(usuario.getSenha());
        parametros.add(usuario.getCpf());
        parametros.add(usuario.getTelefone());
        return parametros;
    }
}
