package edu.fatec.poo.controllers.donation;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.model.produto.Produto;
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

@Getter
@Setter
public class CCadastrarDoacao {
    private final UICoordenador coordenador;
    private final Usuario usuarioLogado;
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
        this(null, doacao, coordenador);
        fromEntity(doacao);
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
    public CCadastrarDoacao(Usuario usuarioLogado, Doacao doacao, UICoordenador coordenador) {
        this.doacao = doacao;
        this.coordenador = coordenador;
        this.usuarioLogado = usuarioLogado;
        date.setValue(LocalDate.now());
    }

    private void fromEntity(Doacao doacao) {
        // TODO
    }

    private Doacao toEntity() {
        return null; // TODO
    }

    public void adicionar() {
        try {
            coordenador.getScene().getRoot().setDisable(true);
            UICadastarDoacaoProduto uiProduto = new UICadastarDoacaoProduto();
            Stage newStage = new Stage();
            uiProduto.start(newStage);
            Produto produtoNovo = uiProduto.getProdutoNovo();
            if (produtoNovo != null) {
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
            listaProdutos.remove(produtoSelecionado.get());
        }
    }

    public void cadastrar() {
        //TODO
        System.out.println("CADASTRAR");
    }

    public void cancelar() {
        System.out.println("CANCELAR");
        // TODO SHOW CONFIMR MESSAGE
        coordenador.returnToPreviosScreen();
    }

    public boolean isEditavel() {
        return usuarioLogado != null;
    }

    public void voltar() {
        coordenador.returnToPreviosScreen();
    }
}
