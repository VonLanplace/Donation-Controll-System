package edu.fatec.poo.views.donation;

import edu.fatec.poo.controllers.donation.CDoacao;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UIDoacao extends Pane {

    private final CDoacao controller;

    public UIDoacao(CDoacao controller) {
        this.controller = controller;
    }
}
