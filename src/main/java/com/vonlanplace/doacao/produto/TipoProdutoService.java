package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.exception.DeleteFailureException;
import com.vonlanplace.doacao.exception.LoadFailureException;
import com.vonlanplace.doacao.exception.SaveFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TipoProdutoService {

    @Autowired
    private TipoProdutoRepository tipoProdutoRepository;

    public void save(TipoProduto tipoProduto){
        try {
            tipoProdutoRepository.save(tipoProduto);
        }catch(Exception e) {
            throw new SaveFailureException(e);
        }
    }

    public Optional<TipoProduto> findById(UUID id){
        try {
            return tipoProdutoRepository.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<TipoProduto> findByNome(String nome){
        try {
            return tipoProdutoRepository.findByName(nome);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void update(TipoProduto tipoProduto){
        if (tipoProdutoRepository.findById(tipoProduto.getId()).isPresent()) {
            try {
                tipoProdutoRepository.save(tipoProduto);
            }catch(Exception e) {
                throw new SaveFailureException();
            }
        } else {
            throw new LoadFailureException();
        }
    }

    public void delete(TipoProduto tipoProduto){
        try {
            tipoProdutoRepository.delete(tipoProduto);
        } catch(Exception e) {
            throw new DeleteFailureException();
        }
    }
}
