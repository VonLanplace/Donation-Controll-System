package edu.fatec.poo.service;

import edu.fatec.poo.adapter.AdapterIn;
import edu.fatec.poo.adapter.user.in.UserIn;
import edu.fatec.poo.exceptions.LoginInvalidoException;
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

    public Usuario login(Usuario usuario) throws SQLException, LoginInvalidoException {
        if (usuario == null) return null;

        Usuario usuarioBD = usuarioDao.searchByEmail(usuario.getEmail());
        if (usuarioBD != null && usuarioBD.getSenha().equals(usuario.getSenha().trim())) {
            return usuarioBD;
        } else {
            throw new LoginInvalidoException("Usuário ou Senha Inválido.");
        }
    }

    public List<Usuario> searchAll() throws SQLException {
        return usuarioDao.searchAll();
    }

    public boolean salvar(Usuario usuario) throws SQLException {
        if (usuario == null) return false;
        Usuario usuarioSalvo = usuarioDao.searchByEmail(usuario.getEmail());
        if (usuarioSalvo == null) {
            usuarioDao.add(usuario);
            return true;
        } else {
            return atualizar(usuario, usuarioSalvo);
        }
    }

    public <S extends UserIn> Usuario salvar(S dtoIn, AdapterIn<S, Usuario> adapterIn) throws SQLException {
        Usuario usuario = adapterIn.toIn(dtoIn);
        salvar(usuario);
        return usuario;
    }

    public boolean deletar(Usuario usuario) throws SQLException {
        if (usuario == null) return false;
        if (usuario.getId() != 0) {
            usuarioDao.delete(usuario);
            return true;
        } else {
            return false;
        }
    }

    public boolean atualizarPorEmail(Usuario atualizado) throws SQLException {
        if (atualizado == null) return false;
        Usuario salvo = usuarioDao.searchByEmail(atualizado.getEmail());
        if (salvo != null) {
            return atualizar(atualizado, salvo);
        } else {
            return false;
        }
    }

    public boolean atualizar(Usuario atualizado, Usuario salvo) throws SQLException {
        if (atualizado == null || salvo == null) return false;

        atualizado.setId(salvo.getId());
        usuarioDao.update(atualizado);
        return true;
    }
}
