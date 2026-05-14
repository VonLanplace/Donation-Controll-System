package edu.fatec.poo.controllers.user;

import edu.fatec.poo.controllers.AController;
import edu.fatec.poo.exceptions.UsuarioNaoCadastradoException;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.connection.CurrentConnection;
import edu.fatec.poo.service.UserService;
import edu.fatec.poo.views.UICoordenador;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CUserLogin extends AController {

    private UserService userService;

    private SimpleStringProperty email;
    private SimpleStringProperty senha;
    private SimpleStringProperty mensagem;

    public CUserLogin(UICoordenador coordenador) {
        super(coordenador);
        try {
            CurrentConnection connection = new CurrentConnection();
            userService = new UserService(connection.getConector());

            email = new SimpleStringProperty();
            senha = new SimpleStringProperty();
            mensagem = new SimpleStringProperty();
        } catch (Exception e) {
            coordenador.showError(e);
        }
    }

    public Usuario login() {
        mensagem.set("");

        Usuario usuario = new Usuario();
        usuario.setSenha(senha.getValue());
        usuario.setEmail(email.getValue());

        try {
            Usuario usuarioLogado = userService.login(usuario);

            if (usuarioLogado == null || usuarioLogado.getAcesso() == null) {
                throw new UsuarioNaoCadastradoException("Usuário ou senha inválidos.");
            }

            clearFields();
            switch (usuarioLogado.getAcesso()) {
                case USER -> {
                    getCoodenator().stashScreen(getCoodenator().getScene().getRoot());
                    getCoodenator().showCadastroDoacaoScreen(usuarioLogado);
                }
                case ADMIN -> {
                    getCoodenator().stashScreen(getCoodenator().getScene().getRoot());
                    getCoodenator().showAdminScreen(usuarioLogado);
                }
                //TODO
                case PUBLIC -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Área pública não disponível neste App", ButtonType.CLOSE);
                    alert.setTitle("Acesso Negado");
                    alert.showAndWait();
                }
            }
        } catch (UsuarioNaoCadastradoException e) {
            mensagem.set(e.getMessage());
        } catch (Exception e) {
            mensagem.set("Erro inesperado ao realizar login.");
            e.printStackTrace();
        }
        return null;
    }

    private void clearFields() {
        email.setValue("");
        senha.setValue("");
    }

}
