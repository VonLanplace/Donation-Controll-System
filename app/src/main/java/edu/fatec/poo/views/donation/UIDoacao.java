package edu.fatec.poo.views.donation;

import edu.fatec.poo.controllers.donation.CDoacao;
import edu.fatec.poo.model.Doacao;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.time.LocalDate;

public class UIDoacao extends BorderPane {

    private final CDoacao controller;

    public UIDoacao(CDoacao controller) {
        this.controller = controller;

        configTabel();
        // configButtons();
    }

    private void configTabel() {
        TableView<Doacao> tbvDoacao = new TableView<>();

        TableColumn<Doacao, LocalDate> colData = new TableColumn<>("Data");
        TableColumn<Doacao, LocalDate> colNome = new TableColumn<>("Nome doador");

        tbvDoacao.getColumns().add(colData);
        tbvDoacao.getColumns().add(colNome);

        this.setTop(tbvDoacao);
    }

    public CDoacao getController() {
        return controller;
    }
}
