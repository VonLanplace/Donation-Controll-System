package edu.fatec.poo.controllers.user;

import edu.fatec.poo.exceptions.UsuarioNaoCadastradoException;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.service.UserService;
import edu.fatec.poo.util.Acesso;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.Data;

@Data
public class CUserLogin {

    UserService userService = new UserService();

    SimpleStringProperty email = new SimpleStringProperty();
    SimpleStringProperty senha = new SimpleStringProperty();
    SimpleStringProperty mensagem = new SimpleStringProperty();

    SimpleObjectProperty<Stage> stage = new SimpleObjectProperty<>();

    public void login() {
        mensagem.set("");

        Usuario usuario = new Usuario();
        usuario.setSenha(senha.getValue());
        usuario.setEmail(email.getValue());

        try {
            Usuario usuarioLogado = userService.login(usuario);

            if (usuarioLogado == null) {
                throw new UsuarioNaoCadastradoException("Usuário ou senha inválidos.");
            }

            redirecionarParaTela(usuarioLogado);

        } catch (UsuarioNaoCadastradoException e) {
            mensagem.set(e.getMessage());
        } catch (Exception e) {
            mensagem.set("Erro inesperado ao realizar login.");
            e.printStackTrace();
        }
    }

    private void redirecionarParaTela(Usuario usuario) throws Exception {
        Application proximaTela = null;

        switch (usuario.getAcesso()) {
            case USER -> proximaTela = new UIUser();
            case ADMIN -> proximaTela = new UIAdim();
            case PUBLIC -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Área pública não disponível neste App", ButtonType.CLOSE);
                alert.setTitle("Acesso Negado");
                alert.showAndWait();
                return;
            }
        }

        if (proximaTela != null && stage.get() != null) {
            proximaTela.start(stage.get());
        }
    }

}
