package edu.fatec.poo.views;

import edu.fatec.poo.controllers.user.CAdmin;
import edu.fatec.poo.controllers.user.CUserLogin;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.views.user.UIAdmin;
import edu.fatec.poo.views.user.UIUserLogin;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.Stack;

import static edu.fatec.poo.configs.WindowStandardFormatting.HEIGHT;
import static edu.fatec.poo.configs.WindowStandardFormatting.WHIDTH;

@Getter
@Setter
public class UICoordenador {
    private Stage stage;
    private Scene scene;
    Stack<Parent> paneStack = new Stack<>();

    public UICoordenador(Stage stage) {
        this.stage = stage;
        scene = new Scene(new Pane(), WHIDTH, HEIGHT);
        paneStack = new Stack<>();
        stage.setScene(scene);
    }

    public void showScreen(Pane view) {
        scene.setRoot(view);
        stage.show();
    }

    public void showLoginScreen() {
        showScreen(new UIUserLogin(new CUserLogin(this)));
    }

    public void showAdminScreen(Usuario usuarioLogado) {
        showScreen(new UIAdmin(new CAdmin(usuarioLogado, this)));
    }

    public void returnToPreviosScreen() {
        Parent previousScreen = paneStack.pop();
        scene.setRoot(previousScreen);
    }

    public void stashScreen(Parent currentView) {
        if (currentView != null)
            paneStack.push(currentView);
    }

    public void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        e.printStackTrace();

        if (e instanceof SQLException sqlEx) {
            alert.setTitle("Erro de Banco de Dados");
            if (sqlEx.getErrorCode() == 1062) {
                alert.setContentText("Erro: Este CPF ou E-mail já está cadastrado.");
            } else {
                alert.setContentText("Falha na conexão: " + sqlEx.getMessage());
            }
        } else if (e instanceof IllegalArgumentException) {
            alert.setTitle("Dados Inválidos");
            alert.setContentText(e.getMessage());
        } else {
            alert.setTitle("Erro");
            alert.setContentText("Um erro inesperado ocorreu: " + e.getMessage());
        }
        alert.showAndWait();
    }
}
