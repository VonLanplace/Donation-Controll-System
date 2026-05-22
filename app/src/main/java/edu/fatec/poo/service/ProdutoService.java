package edu.fatec.poo.service;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.persistence.entityDao.ProdutoDao;

import java.sql.SQLException;
import java.util.List;

public class ProdutoService {
    private ProdutoDao dao;

    public ProdutoService(ProdutoDao dao) {
        this.dao = dao;
    }

    public void save(Produto produto) throws SQLException, ClassNotFoundException {
        if (produto == null) return;

        Produto antigo = dao.searchById(produto.getId());
        if (antigo == null) {
            dao.add(produto);
        } else {
            update(antigo, produto);
        }
    }

    public void save(List<Produto> produtos) throws SQLException, ClassNotFoundException {
        if (produtos == null) return;
        for (Produto p : produtos) {
            save(p);
        }
    }

    public void update(Produto antigo, Produto novo) throws SQLException, ClassNotFoundException {
        if (antigo == null) throw new IllegalArgumentException("Doacao antiga nula");
        if (novo == null) throw new IllegalArgumentException("Doacao nova nula");

        antigo.setDoacao(novo.getDoacao());
        antigo.setMarca(novo.getMarca());
        antigo.setTipo(novo.getTipo());
        antigo.setCesta(novo.getCesta());
        antigo.setCodigoBarras(novo.getCodigoBarras());
        antigo.setDataValidade(novo.getDataValidade());

        dao.update(antigo);
    }

    public List<Produto> searchAllByDoacao(Doacao doacao) throws SQLException, ClassNotFoundException {
        return dao.serarchAllByDoacao(doacao);
    }

    public void delete(Produto produto) throws SQLException, ClassNotFoundException {
        if (produto == null) throw new IllegalArgumentException("Produto nulo");
        if (produto.getId() == null || produto.getId() == 0)
            throw new IllegalArgumentException("Produto com id Inválida");
        dao.delete(produto);
    }

    public void delete(List<Produto> produtos) throws SQLException, ClassNotFoundException {
        if (produtos == null) throw new IllegalArgumentException("Produto nulo");
        for (Produto p : produtos) {
            dao.delete(p);
        }
    }
}
