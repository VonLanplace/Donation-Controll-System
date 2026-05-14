package edu.fatec.poo.adapter.user;

import edu.fatec.poo.adapter.AdapterIn;
import edu.fatec.poo.adapter.user.in.dto.DTOUserAdmin;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.util.Acesso;

public class AdapterCAdmin implements AdapterIn<DTOUserAdmin, Usuario> {
    @Override
    public Usuario toIn(DTOUserAdmin dto) throws IllegalArgumentException {
        Usuario usuario = new Usuario();
        usuario.setAcesso(dto.acesso());
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.getEmail());
        if (dto.resetarSenha()) usuario.resetarSenha();
        usuario.setCpf(dto.cpf());
        usuario.setTelefone(Long.parseLong(dto.telefone()));
        return usuario;
    }
}
