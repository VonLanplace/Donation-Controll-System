package edu.fatec.poo.service;

import edu.fatec.poo.model.produto.TipoProduto;
import edu.fatec.poo.persistence.entityDao.TipoProdutoDao;

import java.sql.SQLException;
import java.util.List;

public class TipoProdutoService {

    private final TipoProdutoDao dao;

    public TipoProdutoService(TipoProdutoDao dao) {
        this.dao = dao;
    }

    public List<TipoProduto> searchAll() throws SQLException, ClassNotFoundException {
        return dao.searchAll();
    }
}
