package edu.fatec.poo.controllers.user;

import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.service.UserService;
import edu.fatec.poo.util.Acesso;
import edu.fatec.poo.views.UICoodenator;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.Data;

import java.sql.SQLException;

@Data
public class CAdmin {

    private Usuario usuario;
    private UICoodenator coodenator;

    private UserService userService;

    // Propriedades para Binding
    private StringProperty nome;
    private StringProperty cpf;
    private StringProperty telefone;
    private StringProperty email;
    private ObjectProperty<Acesso> acesso;
    private BooleanProperty resetarSenha;

    private ObservableList<Usuario> usuariosCadastrados;
    private SimpleObjectProperty<Stage> stage;

    public CAdmin(Usuario usuario, UICoodenator coodenator) {
        try {
            this.userService = new UserService();
            this.coodenator = coodenator;

            nome = new SimpleStringProperty("");
            cpf = new SimpleStringProperty("");
            telefone = new SimpleStringProperty("");
            email = new SimpleStringProperty("");
            acesso = new SimpleObjectProperty<>();
            resetarSenha = new SimpleBooleanProperty(false);

            usuariosCadastrados = FXCollections.observableArrayList(userService.searchAll());
            stage = new SimpleObjectProperty<>();
        } catch (SQLException | ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Falha de Conecção: " + e.getMessage(), ButtonType.CLOSE);
            alert.setTitle("Erro de Acesso");
            alert.showAndWait();
            voltar();
        }
    }

    public void updateUsuarios() {
        try {
            usuariosCadastrados = FXCollections.observableArrayList(userService.searchAll());
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Falha de Conecção: " + e.getMessage(), ButtonType.CLOSE);
            alert.setTitle("Erro de Acesso");
            alert.showAndWait();
            voltar();
        }
    }

    public Usuario toEntity() {
        usuario.setNome(this.nome.get());
        usuario.setCpf(this.cpf.get());
        usuario.setTelefone(Integer.parseInt(this.telefone.get()));
        usuario.setEmail(this.email.get());
        usuario.setAcesso(this.acesso.get());
        return usuario;
    }

    public void fromEntity(Usuario usuario) {
        if (usuario != null) {
            this.nome.set(usuario.getNome() == null ? "" : usuario.getNome());

            // Aplica a máscara no CPF ao carregar do banco para a tela
            this.cpf.set(usuario.getCpf() == null ? "" : usuario.getNome());
            this.telefone.set(String.valueOf(usuario.getTelefone() == 0 ? "" : usuario.getTelefone()));
            this.email.set(usuario.getEmail() == null ? "" : usuario.getEmail());
            this.acesso.set(usuario.getAcesso() == null ? null : usuario.getAcesso());
        }
    }

    public void salvar() {
        userService.salvar(toEntity());
        updateUsuarios();
    }


    public void deletar() {
        userService.deletar(toEntity());
        updateUsuarios();
    }

    public void voltar() {
        try {
            coodenator.returnToPreviosScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void limpar() {
        usuario = new Usuario();
        fromEntity(usuario);
    }
}