package edu.fatec.poo.service;

import edu.fatec.poo.exceptions.LoginInválidoException;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.ADaoConnection;
import edu.fatec.poo.persistence.CurrentConnection;
import edu.fatec.poo.persistence.entityDao.UsuarioDao;

import java.sql.SQLException;

public class UserService {

    ADaoConnection connection;

    public Usuario login(Usuario usuario) throws SQLException, ClassNotFoundException {
        connection = CurrentConnection.connection;
        UsuarioDao dao = new UsuarioDao(connection);
        Usuario usuarioBD = dao.searchByEmail(usuario.getEmail());
        if (usuarioBD != null && usuarioBD.getSenha().equals(usuario.getSenha().trim())) {
            return usuarioBD;
        } else {
            throw new LoginInválidoException("Usuário ou Senha Inválido.");
        }
    }
}
