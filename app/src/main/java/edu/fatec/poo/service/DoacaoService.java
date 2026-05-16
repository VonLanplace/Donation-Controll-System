package edu.fatec.poo.service;

import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.persistence.DoacaoDao;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class DoacaoService {
    private DoacaoDao dao;

    public DoacaoService(DoacaoDao dao) {
        this.dao = dao;
    }


    public void save(Doacao doacao) throws SQLException, ClassNotFoundException {
        if (doacao == null) return;

        Doacao antigo = dao.searchById(doacao.getId());
        if (antigo == null) {
            dao.add(doacao);
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
    }

    public List<Doacao> searchAll() throws SQLException, ClassNotFoundException {
        return dao.searchAll();
    }

    public List<Doacao> searchLastInt(int n) throws SQLException, ClassNotFoundException {
        return dao.searchLastNByDate(n);
    }

    public List<Doacao> searchByLikeName(String s) throws SQLException, ClassNotFoundException {
        return dao.searchByLikeName(s);
    }

    public void deleteById(Doacao doacao) throws SQLException, ClassNotFoundException {
        if (doacao != null && doacao.getId() != null && doacao.getId() != 0) {
            dao.delete(doacao);
        }
    }
}
