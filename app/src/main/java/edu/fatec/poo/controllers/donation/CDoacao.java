package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.views.UICoordenador;

public class CDoacao {
    private final UICoordenador coordenador;
    private final Usuario usuarioLogado;

    public CDoacao(Usuario usuarioLogado, UICoordenador coordenador) {
        this.usuarioLogado = usuarioLogado;
        this.coordenador = coordenador;
    }

    public UICoordenador getCoordenador() {
        return coordenador;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}
