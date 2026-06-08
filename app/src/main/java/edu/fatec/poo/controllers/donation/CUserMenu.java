package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.persistence.connection.CurrentConnection;
import edu.fatec.poo.persistence.entityDao.DoacaoDao;
import edu.fatec.poo.persistence.entityDao.ProdutoDao;
import edu.fatec.poo.service.DoacaoService;
import edu.fatec.poo.service.ProdutoService;
import edu.fatec.poo.views.UICoordenador;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

            ProdutoDao pDao = new ProdutoDao(connector.getConector());
            ProdutoService pService = new ProdutoService(pDao);

            DoacaoDao dao = new DoacaoDao(connector.getConector());
            this.doacaoService = new DoacaoService(dao, pService);

            doacaos.setAll(dao.searchLastNByDate(7));
        } catch (Exception e) {
            coordenador.showError(e);
        }
    }

    public void update() {
        try {
            doacaos.setAll(doacaoService.searchLastNByDate(7));
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
            try {
                doacaos.setAll(doacaoService.searchByLikeName(nomePesquisa.get()));
            } catch (Exception e) {
                coordenador.showError(e);
            }
        }
    }

    public void limpar() {
        nomePesquisa.setValue("");
        doacaoSelecionada.setValue(null);
    }

    public UICoordenador getCoordenador() {
        return coordenador;
    }

    public void setCoordenador(UICoordenador coordenador) {
        this.coordenador = coordenador;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public DoacaoService getDoacaoService() {
        return doacaoService;
    }

    public void setDoacaoService(DoacaoService doacaoService) {
        this.doacaoService = doacaoService;
    }

    public ObservableList<Doacao> getDoacaos() {
        return doacaos;
    }

    public void setDoacaos(ObservableList<Doacao> doacaos) {
        this.doacaos = doacaos;
    }

    public Doacao getDoacaoSelecionada() {
        return doacaoSelecionada.get();
    }

    public void setDoacaoSelecionada(Doacao doacaoSelecionada) {
        this.doacaoSelecionada.set(doacaoSelecionada);
    }

    public SimpleObjectProperty<Doacao> doacaoSelecionadaProperty() {
        return doacaoSelecionada;
    }

    public String getNomePesquisa() {
        return nomePesquisa.get();
    }

    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa.set(nomePesquisa);
    }

    public SimpleStringProperty nomePesquisaProperty() {
        return nomePesquisa;
    }
}
