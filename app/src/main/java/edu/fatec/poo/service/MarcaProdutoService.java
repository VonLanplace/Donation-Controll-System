package edu.fatec.poo.service;

import edu.fatec.poo.model.produto.MarcaProduto;
import edu.fatec.poo.persistence.entityDao.MarcaProdutoDao;

import java.sql.SQLException;
import java.util.List;

public class MarcaProdutoService {

    private final MarcaProdutoDao dao;

    public MarcaProdutoService(MarcaProdutoDao dao) {
        this.dao = dao;
    }

    public List<MarcaProduto> searchAll() throws SQLException, ClassNotFoundException {
        for (MarcaProduto m : dao.searchAll()) {
            System.out.println(m);
        }
        return dao.searchAll();
    }
}
