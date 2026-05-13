package edu.fatec.poo.adapter.user;

import edu.fatec.poo.adapter.AdapterIn;
import edu.fatec.poo.adapter.user.in.dto.DTOUserAdmin;
import edu.fatec.poo.model.Usuario;

public class AdapterUserAdmin implements AdapterIn<DTOUserAdmin, Usuario> {
    @Override
    public Usuario toIn(DTOUserAdmin object) throws IllegalArgumentException {
        return null; //TODO
    }
}
