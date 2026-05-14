package edu.fatec.poo.controllers;

import edu.fatec.poo.views.UICoordenador;
import lombok.Data;

@Data
public abstract class AController {
    private UICoordenador coodenator;

    public AController(UICoordenador coodenator) {
        this.coodenator = coodenator;
    }

    public void voltar() {
        coodenator.returnToPreviosScreen();
    }
}
