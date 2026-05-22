package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.persistence.connection.CurrentConnection;
import edu.fatec.poo.persistence.entityDao.DoacaoDao;
import edu.fatec.poo.persistence.entityDao.ProdutoDao;
import edu.fatec.poo.service.DoacaoService;
import edu.fatec.poo.service.ProdutoService;
import edu.fatec.poo.views.UICoordenador;
import edu.fatec.poo.views.donation.UICadastarDoacaoProduto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
public class CCadastrarDoacao {
    private final UICoordenador coordenador;
    private final Usuario usuarioLogado;
    private DoacaoService doacaoService;
    private Doacao doacao;

    private StringProperty nomeDoador = new SimpleStringProperty();
    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    private ObjectProperty<Produto> produtoSelecionado = new SimpleObjectProperty<>();
    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    /**
     * Construtor utilizado para a visualização de uma doação existente.
     * Este construtor inicializa o controlador focando na exibição dos dados da doação
     * sem a necessidade de um usuário logado vinculado à ação imediata.
     *
     * @param doacao      A instância da {@link Doacao} que será lida.
     * @param coordenador A instância da interface de usuário {@link UICoordenador} que gerencia o fluxo.
     */
    public CCadastrarDoacao(Doacao doacao, UICoordenador coordenador) {
        this(null, doacao.getId(), coordenador);
    }

    /**
     * Construtor utilizado para o processo de criação de uma nova doação.
     * Define o contexto da operação com base no usuário que está realizando o cadastro.
     *
     * @param usuarioLogado O {@link Usuario} que está autenticado e realizando a operação.
     * @param coordenador   A instância do {@link UICoordenador} responsável pela navegação.
     */
    public CCadastrarDoacao(Usuario usuarioLogado, UICoordenador coordenador) {
        this(usuarioLogado, null, coordenador);
    }

    /**
     * Construtor mestre utilizado para a atualização de uma doação ou inicialização completa.
     * Permite associar simultaneamente o autor da modificação, a doação alvo e o coordenador de interface.
     *
     * @param usuarioLogado O {@link Usuario} que está operando o sistema.
     * @param doacao        A {@link Doacao} a ser manipulada ou atualizada.
     * @param coordenador   A instância de {@link UICoordenador} para controle de fluxo da UI.
     */
    public CCadastrarDoacao(Usuario usuarioLogado, UUID doacaoUuid, UICoordenador coordenador) {
        this.coordenador = coordenador;
        this.usuarioLogado = usuarioLogado;

        date.setValue(LocalDate.now());
        try {
            CurrentConnection connector = new CurrentConnection();

            ProdutoDao pDao = new ProdutoDao(connector.getConector());
            ProdutoService pSer = new ProdutoService(pDao);

            DoacaoDao dao = new DoacaoDao(connector.getConector());
            doacaoService = new DoacaoService(dao, pSer);

            System.out.println("Doacao: " + doacaoUuid);
            System.out.println("User: " + usuarioLogado);
            this.doacao = doacaoService.searchByUUID(doacaoUuid);
            if (doacao == null) {
                this.doacao = new Doacao();
            }
            System.out.println(doacao.getId().toString());
            doacaoService.loadProdutos(doacao);
            fromEntity(doacao);

        } catch (Exception e) {
            coordenador.showError(e);
        }
    }

    private void fromEntity(Doacao doacao) {
        nomeDoador.set(doacao.getNomeDoador() == null ? "" : doacao.getNomeDoador());
        date.set(doacao.getData() == null ? LocalDate.now() : doacao.getData());
        listaProdutos.setAll(doacao.getProdutos() == null || doacao.getProdutos().isEmpty()
                ? new ArrayList<Produto>() : doacao.getProdutos());
        this.doacao = doacao;
    }

    private Doacao toEntity() {
        if (this.doacao == null) return new Doacao();
        Doacao novaDoacao = new Doacao();
        novaDoacao.setId(doacao.getId());
        novaDoacao.setData(date.get());
        novaDoacao.setCadastrante(usuarioLogado);
        novaDoacao.setProdutos(listaProdutos);
        novaDoacao.setNomeDoador(nomeDoador.get());
        for (Produto p : listaProdutos) {
            p.setDoacao(novaDoacao);
        }
        return novaDoacao;
    }

    public void adicionar() {
        try {
            coordenador.getScene().getRoot().setDisable(true);
            UICadastarDoacaoProduto uiProduto = new UICadastarDoacaoProduto();
            Stage newStage = new Stage();
            uiProduto.start(newStage);
            Produto produtoNovo = uiProduto.getProdutoNovo();
            if (produtoNovo != null) {
                produtoNovo.setDoacao(doacao);
                listaProdutos.add(produtoNovo);
            }
        } catch (Exception e) {
            coordenador.showError(e);
        } finally {
            coordenador.getScene().getRoot().setDisable(false);
        }
    }

    public void remover() {
        if (produtoSelecionado != null) {
            try {
                listaProdutos.remove(produtoSelecionado.get());
            } catch (Exception e) {
                coordenador.showError(e);
            }
        }
    }

    public void cadastrar() {
        if (doacao != null) {
            try {
                doacao = toEntity();
                doacaoService.save(doacao);
                coordenador.returnToPreviosScreen();
            } catch (Exception e) {
                coordenador.showError(e);
            }
        }
    }

    public void cancelar() {
        System.out.println("CANCELAR");
        coordenador.returnToPreviosScreen();
    }

    public boolean isEditavel() {
        return usuarioLogado != null;
    }

    public boolean isSaved() {
        return doacao != null && doacao.getId() != null;
    }


    public void voltar() {
        coordenador.returnToPreviosScreen();
    }

    public void deletar() {
        if (doacao != null && doacao.getId() != null) {
            try {
                doacaoService.deleteById(doacao);
                coordenador.returnToPreviosScreen();
            } catch (Exception e) {
                coordenador.showError(e);
            }
        }
    }
}
