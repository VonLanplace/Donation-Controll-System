package edu.fatec.poo.controllers;

import edu.fatec.poo.views.UICoodenator;
import lombok.Data;

@Data
public abstract class AController {
    private UICoodenator coodenator;

    public AController(UICoodenator coodenator) {
        this.coodenator = coodenator;
    }

    public void voltar() {
        coodenator.returnToPreviosScreen();
    }
}
