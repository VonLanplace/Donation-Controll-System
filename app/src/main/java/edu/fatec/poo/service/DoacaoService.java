package edu.fatec.poo.service;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.produto.Produto;
import edu.fatec.poo.persistence.entityDao.DoacaoDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoacaoService {

    private ProdutoService produtoService;
    private DoacaoDao dao;

    public DoacaoService(DoacaoDao dao, ProdutoService produtoService) {
        this.dao = dao;
        this.produtoService = produtoService;
    }


    public void save(Doacao doacao) throws SQLException, ClassNotFoundException {
        if (doacao == null) return;

        for (Produto p : doacao.getProdutos()) {
            p.setDoacao(doacao);
        }

        Doacao antigo = dao.searchById(doacao.getId());
        if (antigo == null) {
            dao.add(doacao);

            produtoService.save(doacao.getProdutos());
        } else {
            update(antigo, doacao);
        }
    }

    public void update(Doacao antigo, Doacao novo) throws SQLException, ClassNotFoundException {
        if (antigo == null) throw new IllegalArgumentException("Doacao antiga nula");
        if (novo == null) throw new IllegalArgumentException("Doacao nova nula");

        antigo.setProdutos(novo.getProdutos());
        antigo.setData(novo.getData());
        antigo.setCadastrante(novo.getCadastrante());
        antigo.setNomeDoador(novo.getNomeDoador());

        dao.update(antigo);
        for (Produto p : antigo.getProdutos()) {
            p.setDoacao(antigo);
        }
        produtoService.save(antigo.getProdutos());
    }

    public List<Doacao> searchAll() throws SQLException, ClassNotFoundException {
        return loadProdutos(dao.searchAll());
    }

    public List<Doacao> searchLastInt(int n) throws SQLException, ClassNotFoundException {
        return loadProdutos(dao.searchLastNByDate(n));
    }

    public List<Doacao> searchByLikeName(String s) throws SQLException, ClassNotFoundException {
        return loadProdutos(dao.searchByName(s));
    }

    public List<Doacao> loadProdutos(List<Doacao> doacoes) throws SQLException, ClassNotFoundException {
        for (Doacao d : doacoes) {
            d.setProdutos(produtoService.searchAllByDoacao(d));
        }
        return doacoes;
    }

    public void delete(Doacao doacao) throws SQLException, ClassNotFoundException {
        deleteById(doacao);
    }

    public void deleteById(Doacao doacao) throws SQLException, ClassNotFoundException {
        if (doacao != null && doacao.getId() != null) {
            for (Produto p : doacao.getProdutos()) {
                produtoService.delete(p);
            }
            dao.delete(doacao);
        }
    }

    public Doacao loadProdutos(Doacao doacao) throws SQLException, ClassNotFoundException {
        doacao.setProdutos(produtoService.searchAllByDoacao(doacao));
        return doacao;
    }

    public List<Doacao> searchLastNByDate(int i) throws SQLException, ClassNotFoundException {
        if (i == 0) return new ArrayList<>();
        return dao.searchLastNByDate(5);
    }
}
