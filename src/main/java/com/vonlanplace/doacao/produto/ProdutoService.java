package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.exception.DeleteFailureException;
import com.vonlanplace.doacao.exception.LoadFailureException;
import com.vonlanplace.doacao.exception.ObjectNotFoundException;
import com.vonlanplace.doacao.exception.SaveFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Produto findByCodigoBarras(String codigoBarras) throws LoadFailureException, ObjectNotFoundException {
        try {
            Optional<Produto> produto = produtoRepository.findByCodigoBarras(codigoBarras);
            if (produto.isPresent()) {
                return produto.get();
            } else {
                throw new ObjectNotFoundException();
            }
        } catch (Exception e) {
            throw new LoadFailureException();
        }
    }

    public List<Produto> findAll() throws LoadFailureException {
        try {
            return produtoRepository.findAll();
        } catch (Exception e) {
            throw new LoadFailureException();
        }
    }

    public List<Produto> findByMarcaProduto(MarcaProduto marcaProduto) throws LoadFailureException {
        try {
            return produtoRepository.findAllByMarcaProduto(marcaProduto);
        } catch (Exception e) {
            throw new LoadFailureException();
        }
    }

    public List<Produto> findByTipoProduto(TipoProduto tipoProduto) throws LoadFailureException {
        try {
            return produtoRepository.findAllByTipoProduto(tipoProduto);
        } catch (Exception e) {
            throw new LoadFailureException();
        }
    }

    public void save(Produto produto) throws SaveFailureException {
        try {
            produtoRepository.save(produto);
        } catch (Exception e) {
            throw new SaveFailureException();
        }
    }

    public void update(Produto produto) throws SaveFailureException, ObjectNotFoundException {
        if (produtoRepository.findByCodigoBarras(produto.getCodigoBarras()).isPresent()) {
            try {
                produtoRepository.save(produto);
            } catch (Exception e) {
                throw new SaveFailureException();
            }
        } else {
            throw new ObjectNotFoundException();
        }
    }

    public void delete(Produto produto) throws DeleteFailureException {
        try {
            produtoRepository.delete(produto);
        } catch (Exception e) {
            throw new DeleteFailureException();
        }
    }
}
