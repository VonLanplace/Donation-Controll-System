package edu.fatec.poo.persistence.entityDao;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.Usuario;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.persistence.connection.ADaoConnector;
import edu.fatec.poo.util.Acesso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProdutoDao extends GenericDao<Produto> {
    public ProdutoDao(ADaoConnector aDaoConnector) throws SQLException, ClassNotFoundException {
        super(aDaoConnector, "produto");

    }

    /*
    private Long id;
    private Doacao doacao;
    private MarcaProduto marca;
    private TipoProduto tipo;
    private Cesta cesta;
    private String codigoBarras;
    private LocalDate dataValidade;
    */
    @Override
    protected Produto map(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getLong("id_produto"));
        produto.setDoacao(new Doacao(
                rs.getLong("id_doacao"),
                rs.getString("nome_doador"),
                rs.getDate("data_doacao").toLocalDate(),
                new Usuario(
                        rs.getLong("id_cadastrante"),
                        Acesso.getAcesso(rs.getInt("acesso_cadastrante")),
                        rs.getString("nome_cadastrante"),
                        rs.getString("email_cadastrante"),
                        rs.getString("senha_cadastrante"),
                        rs.getString("cpf_cadastrante"),
                        rs.getLong("telefone_cadastrante")
                ),
                List.of(produto)
        ));
        produto.getDoacao().setId(rs.getLong("doacao"));
        produto.setTipo();
        return produto;
    }

    @Override
    protected List<Object> getAtributos(Produto object) {
        return List.of();
    }

    @Override
    public Produto add(Produto object) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public void update(Produto object) throws SQLException, ClassNotFoundException {

    }
}
