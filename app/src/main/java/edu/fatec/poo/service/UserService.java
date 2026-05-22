package edu.fatec.poo.service;

import edu.fatec.poo.adapter.AdapterIn;
import edu.fatec.poo.adapter.user.in.UserIn;
import edu.fatec.poo.exceptions.LoginInvalidoException;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.connection.ADaoConnector;
import edu.fatec.poo.persistence.entityDao.UsuarioDao;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UsuarioDao usuarioDao;

    public UserService(ADaoConnector connection) throws SQLException, ClassNotFoundException {
        usuarioDao = new UsuarioDao(connection);
    }

    public Usuario login(Usuario usuario) throws SQLException, LoginInvalidoException, ClassNotFoundException {
        if (usuario == null || usuario.getSenha() == null) return null;

        Usuario usuarioBD = usuarioDao.searchByEmail(usuario.getEmail());
        if (usuarioBD != null && usuarioBD.getSenha() != null
                && usuarioBD.getSenha().equals(usuario.getSenha().trim())) {
            return usuarioBD;
        } else {
            throw new LoginInvalidoException("Usuário ou Senha Inválido.");
        }
    }

    public List<Usuario> searchAll() throws SQLException, ClassNotFoundException {
        return usuarioDao.searchAll();
    }

    public Usuario findById(long id) throws SQLException, ClassNotFoundException {
        return usuarioDao.searchById(id);
    }

    public Usuario findByEmail(String email) throws SQLException, ClassNotFoundException {
        if (email.length() > 255) return null;
        return usuarioDao.searchByEmail(email);
    }

    public void salvar(Usuario usuario) throws SQLException, ClassNotFoundException {
        if (usuario == null) return;
        Usuario usuarioSalvo = usuarioDao.searchByEmail(usuario.getEmail());

        if (usuarioSalvo == null) {
            usuarioSalvo = usuarioDao.searchByCpf(usuario.getCpf());
        }

        if (usuarioSalvo == null) {
            if (usuario.getSenha() == null) usuario.resetarSenha();
            usuarioDao.add(usuario);
        } else {
            atualizar(usuario, usuarioSalvo);
        }
    }

    public <S extends UserIn> void salvar(S dtoIn, AdapterIn<S, Usuario> adapterIn) throws SQLException, ClassNotFoundException {
        Usuario usuario = adapterIn.toIn(dtoIn);
        salvar(usuario);
    }

    public void deletar(Usuario usuario) throws SQLException, ClassNotFoundException {
        if (usuario == null) return;
        if (usuario.getId() != 0) {
            usuarioDao.delete(usuario);
        }
    }

    public boolean atualizarPorEmail(Usuario atualizado) throws SQLException, ClassNotFoundException {
        if (atualizado == null) return false;
        Usuario salvo = usuarioDao.searchByEmail(atualizado.getEmail());
        if (salvo != null) {
            return atualizar(atualizado, salvo);
        } else {
            return false;
        }
    }

    public boolean atualizar(Usuario atualizado, Usuario salvo) throws SQLException, ClassNotFoundException {
        if (atualizado == null || salvo == null) return false;

        atualizado.setId(salvo.getId());
        if (atualizado.getId() == 0) atualizado.setId(salvo.getId());
        if (atualizado.getAcesso() == null) atualizado.setAcesso(salvo.getAcesso());
        if (atualizado.getNome() == null || atualizado.getNome().isBlank()) atualizado.setNome(salvo.getNome());
        if (atualizado.getEmail() == null || atualizado.getEmail().isBlank()) atualizado.setEmail(salvo.getEmail());
        if (atualizado.getSenha() == null || atualizado.getSenha().isBlank()) atualizado.setSenha(salvo.getSenha());
        usuarioDao.update(atualizado);
        return true;
    }
}
