package edu.fatec.poo.controllers.user;

import edu.fatec.poo.adapter.user.AdapterCAdmin;
import edu.fatec.poo.adapter.user.in.dto.DTOUserAdmin;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.connection.CurrentConnection;
import edu.fatec.poo.service.UserService;
import edu.fatec.poo.util.Acesso;
import edu.fatec.poo.views.UICoordenador;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import lombok.Data;

import java.util.List;

@Data
public class CAdmin {

    private Usuario usuario;
    private Usuario usuarioLogado;
    private UICoordenador coodenator;

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

    public CAdmin(Usuario usuario, UICoordenador coodenator) {
        try {
            CurrentConnection connection = new CurrentConnection();
            this.userService = new UserService(connection.getConector());
            this.coodenator = coodenator;
            this.usuarioLogado = usuario;

            nome = new SimpleStringProperty("");
            cpf = new SimpleStringProperty("");
            telefone = new SimpleStringProperty("");
            email = new SimpleStringProperty("");
            acesso = new SimpleObjectProperty<>();
            resetarSenha = new SimpleBooleanProperty(false);

            usuariosCadastrados = FXCollections.observableArrayList(userService.searchAll());
            stage = new SimpleObjectProperty<>();
        } catch (Exception e) {
            coodenator.showError(e);
            voltar();
        }
    }

    public void updateUsuarios() {
        try {
            List<Usuario> lista = userService.searchAll();
            if (lista != null) {
                usuariosCadastrados.setAll(lista);
            }
        } catch (Exception e) {
            coodenator.showError(e);
        }
    }

    public Usuario toEntity() {
        if (this.usuario == null) this.usuario = new Usuario();

        usuario.setNome(this.nome.get());
        usuario.setCpf(this.cpf.get());

        String telString = this.telefone.get();
        try {
            usuario.setTelefone(telString == null || telString.isEmpty() ? 0 : Long.parseLong(telString.replaceAll("[^0-9]", "")));
        } catch (NumberFormatException e) {
            usuario.setTelefone(0);
        }

        usuario.setEmail(this.email.get());
        usuario.setAcesso(this.acesso.get());

        String senhaPadrao = "12345678";

        if (resetarSenha.get() || usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            usuario.setSenha(senhaPadrao);
        }
        return usuario;
    }

    public void fromEntity(Usuario usuario) {
        if (usuario != null) {
            this.nome.set(usuario.getNome() == null ? "" : usuario.getNome());
            this.cpf.set(usuario.getCpf() == null ? "" : usuario.getCpf());
            this.telefone.set(String.valueOf(usuario.getTelefone() == 0 ? "" : usuario.getTelefone()));
            this.email.set(usuario.getEmail() == null ? "" : usuario.getEmail());
            this.acesso.set(usuario.getAcesso() == null ? null : usuario.getAcesso());
            this.resetarSenha.set(false);
        } else {
            limpar();
        }
    }

    public void salvar() {
        if (this.usuario == null) {
            this.usuario = new Usuario();
        }
        try {
            userService.salvar(new DTOUserAdmin(
                    nome.get(),
                    cpf.get(),
                    telefone.get(),
                    email.get(),
                    acesso.get(),
                    resetarSenha.get()
            ), new AdapterCAdmin());
            usuario = userService.findByEmail(nome.get());
            fromEntity(usuario);
            updateUsuarios();
        } catch (Exception e) {
            coodenator.showError(e);
        }
    }


    public void deletar() {
        try {
            if (usuario == null || usuario.getId() == 0) return;
            if (usuario.getId() == usuarioLogado.getId())
                throw new IllegalArgumentException("Usuário não pode se Deletar do sistema.");

            Stage stage = coodenator.getStage();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente deletar?", ButtonType.YES, ButtonType.NO);
            alert.initOwner(stage);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (usuario != null && usuario.getId() != null) {
                        try {
                            userService.deletar(usuario);
                            updateUsuarios();
                            limpar();
                        } catch (Exception e) {
                            coodenator.showError(e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            coodenator.showError(e);
        }
    }

    public void voltar() {
        coodenator.returnToPreviosScreen();
    }

    public void limpar() {
        usuario = null;
        nome.setValue("");
        cpf.setValue("");
        telefone.setValue("");
        email.setValue("");
        acesso.setValue(Acesso.PUBLIC);
        resetarSenha.setValue(false);
    }

    public void select(Usuario usuario) {
        this.usuario = usuario;
        fromEntity(usuario);
    }
}