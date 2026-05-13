package edu.fatec.poo.service;

import edu.fatec.poo.exceptions.LoginInválidoException;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.CurrentConnection;
import edu.fatec.poo.persistence.entityDao.UsuarioDao;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UsuarioDao usuarioDao;

    public UserService() throws SQLException, ClassNotFoundException {
        usuarioDao = new UsuarioDao(CurrentConnection.connection);
    }

    public Usuario login(Usuario usuario) throws SQLException {
        if (usuario == null) return null;

        Usuario usuarioBD = usuarioDao.searchByEmail(usuario.getEmail());
        if (usuarioBD != null && usuarioBD.getSenha().equals(usuario.getSenha().trim())) {
            return usuarioBD;
        } else {
            throw new LoginInválidoException("Usuário ou Senha Inválido.");
        }
    }

    public List<Usuario> searchAll() throws SQLException {
        return usuarioDao.searchAll();
    }

    public boolean salvar(Usuario usuario) {
        if (usuario == null) return false;
        try {
            Usuario usuarioSalvo = usuarioDao.searchByEmail(usuario.getEmail());
            if (usuarioSalvo == null) {
                usuarioDao.add(usuario);
                return true;
            } else {
                return atualizar(usuario, usuarioSalvo);
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deletar(Usuario usuario) {
        if (usuario == null) return false;
        try {
            if (usuario.getId() != 0) {
                usuarioDao.delete(usuario);
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean atualizarPorEmail(Usuario atualizado) {
        if (atualizado == null) return false;
        try {
            Usuario salvo = usuarioDao.searchByEmail(atualizado.getEmail());
            if (salvo != null) {
                return atualizar(atualizado, salvo);
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean atualizar(Usuario atualizado, Usuario salvo) {
        if (atualizado == null || salvo == null) return false;

        atualizado.setId(salvo.getId());
        try {
            usuarioDao.update(atualizado);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
