package com.vonlanplace.doacao.produto.tipo;

import com.vonlanplace.doacao.exception.DeleteFailureException;
import com.vonlanplace.doacao.exception.LoadFailureException;
import com.vonlanplace.doacao.exception.ObjectNotFoundException;
import com.vonlanplace.doacao.exception.SaveFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TipoProdutoService {

    @Autowired
    private TipoProdutoRepository tipoProdutoRepository;

    public void save(TipoProduto tipoProduto) throws SaveFailureException {
        try {
            tipoProdutoRepository.save(tipoProduto);
        } catch (Exception e) {
            throw new SaveFailureException(e);
        }
    }

    public TipoProduto findById(UUID id) throws ObjectNotFoundException, LoadFailureException {
        try {
            Optional<TipoProduto> tipoProduto = tipoProdutoRepository.findById(id);
            if (tipoProduto.isPresent()) {
                return tipoProduto.get();
            } else {
                throw new ObjectNotFoundException();
            }
        } catch (Exception e) {
            throw new LoadFailureException();
        }
    }

    public TipoProduto findByNome(String nome) throws ObjectNotFoundException, LoadFailureException {
        try {
            Optional<TipoProduto> tipoProduto = tipoProdutoRepository.findByNome(nome);
            if (tipoProduto.isPresent()) {
                return tipoProduto.get();
            } else {
                throw new ObjectNotFoundException();
            }
        } catch (Exception e) {
            throw new LoadFailureException();
        }
    }

    public void update(TipoProduto tipoProduto) throws SaveFailureException, ObjectNotFoundException {
        if (tipoProdutoRepository.findById(tipoProduto.getId()).isPresent()) {
            try {
                tipoProdutoRepository.save(tipoProduto);
            } catch (Exception e) {
                throw new SaveFailureException();
            }
        } else {
            throw new ObjectNotFoundException();
        }
    }

    public void delete(TipoProduto tipoProduto) throws DeleteFailureException {
        try {
            tipoProdutoRepository.delete(tipoProduto);
        } catch (Exception e) {
            throw new DeleteFailureException();
        }
    }
}
