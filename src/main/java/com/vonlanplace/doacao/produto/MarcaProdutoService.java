package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.exception.DeleteFailureException;
import com.vonlanplace.doacao.exception.LoadFailureException;
import com.vonlanplace.doacao.exception.SaveFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MarcaProdutoService {

    @Autowired
    private MarcaProdutoRepository marcaProdutoRepository;

    public void save(MarcaProduto marcaProduto){
        try {
            marcaProdutoRepository.save(marcaProduto);
        }catch(Exception e) {
            throw new SaveFailureException(e);
        }
    }

    public Optional<MarcaProduto> findById(UUID id){
        try {
            return marcaProdutoRepository.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<MarcaProduto> findByNome(String nome){
        try {
            return marcaProdutoRepository.findByName(nome);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void update(MarcaProduto marcaProduto){
        if (marcaProdutoRepository.findById(marcaProduto.getId()).isPresent()) {
            try {
                marcaProdutoRepository.save(marcaProduto);
            }catch(Exception e) {
                throw new SaveFailureException();
            }
        } else {
            throw new LoadFailureException();
        }
    }

    public void delete(MarcaProduto marcaProduto){
        try {
            marcaProdutoRepository.delete(marcaProduto);
        } catch(Exception e) {
            throw new DeleteFailureException();
        }
    }
}
