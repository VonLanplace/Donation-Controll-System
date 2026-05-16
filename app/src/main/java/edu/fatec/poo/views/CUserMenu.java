package edu.fatec.poo.views;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.DoacaoDao;
import edu.fatec.poo.persistence.connection.CurrentConnection;
import edu.fatec.poo.service.DoacaoService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.sql.SQLException;

@Getter
@Setter
public class CUserMenu {

    private UICoordenador coordenador;
    private Usuario usuarioLogado;
    private DoacaoService doacaoService;

    private ObservableList<Doacao> doacaos = FXCollections.observableArrayList();

    private SimpleObjectProperty<Doacao> doacaoSelecionada = new SimpleObjectProperty<>();
    private SimpleStringProperty nomePesquisa = new SimpleStringProperty();

    public CUserMenu(Usuario usuarioLogado, UICoordenador uiCoordenador) {
        try {
            this.coordenador = uiCoordenador;
            this.usuarioLogado = usuarioLogado;
            CurrentConnection connector = new CurrentConnection();
            DoacaoDao dao = new DoacaoDao(connector.getConector());
            this.doacaoService = new DoacaoService(dao);

            doacaos.setAll(dao.searchLastNByDate(5));
        } catch (Exception e) {
            coordenador.showError(e);
        }
    }

    public void novo() {
        coordenador.stashScreen();
        coordenador.showCadastroDoacaoScreen(usuarioLogado);
    }

    public void editar() {
        if (doacaoSelecionada.get() != null) {
            coordenador.stashScreen();
            coordenador.showUpdateDoacaoScreen(usuarioLogado, doacaoSelecionada.get());
        } else {
            coordenador.showError(
                    new IllegalArgumentException("Doaçâo nâo selecionada")
            );
        }
    }

    public void ver() {
        if (doacaoSelecionada.get() != null) {
            coordenador.stashScreen();
            coordenador.showReadDoacaoScreen(doacaoSelecionada.get());
        } else {
            coordenador.showError(
                    new IllegalArgumentException("Doaçâo nâo selecionada")
            );
        }
    }

    public void voltar() {
        coordenador.returnToPreviosScreen();
    }

    public void pesquisar() {
        if (nomePesquisa.get() != null && !nomePesquisa.get().isEmpty()) {
            doacaos.setAll(doacaoService.searchByLikeName(nomePesquisa.get()));
        }
    }

    public void limpar() {
        nomePesquisa.setValue("");
        doacaoSelecionada.setValue(null);
    }
}
