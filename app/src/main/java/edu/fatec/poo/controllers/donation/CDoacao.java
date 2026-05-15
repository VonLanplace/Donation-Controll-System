package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.views.UICoordenador;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CDoacao {
    private final UICoordenador coordenador;
    private final Usuario usuarioLogado;

    public CDoacao(Usuario usuarioLogado, UICoordenador coordenador) {
        this.usuarioLogado = usuarioLogado;
        this.coordenador = coordenador;
    }
}
