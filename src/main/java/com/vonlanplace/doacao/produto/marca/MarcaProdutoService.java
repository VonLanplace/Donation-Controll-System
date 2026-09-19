package com.vonlanplace.doacao.produto.marca;

import com.vonlanplace.doacao.exception.DeleteFailureException;
import com.vonlanplace.doacao.exception.LoadFailureException;
import com.vonlanplace.doacao.exception.ObjectNotFoundException;
import com.vonlanplace.doacao.exception.SaveFailureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MarcaProdutoService {

    @Autowired
    private MarcaProdutoRepository marcaProdutoRepository;

    public void save(MarcaProduto marcaProduto) throws SaveFailureException {
        try {
            marcaProdutoRepository.save(marcaProduto);
        } catch (Exception e) {
            throw new SaveFailureException(e);
        }
    }

    public MarcaProduto findById(UUID id) throws ObjectNotFoundException, LoadFailureException {
        try {
            Optional<MarcaProduto> marcaProduto = marcaProdutoRepository.findById(id);
            if (marcaProduto.isPresent()) {
                return marcaProduto.get();
            } else {
                throw new ObjectNotFoundException();
            }
        } catch (Exception e) {
            throw new LoadFailureException();
        }
    }

    public MarcaProduto findByNome(String nome) throws ObjectNotFoundException, LoadFailureException {
        try {
            Optional<MarcaProduto> marcaProduto = marcaProdutoRepository.findByNome(nome);
            if (marcaProduto.isPresent()) {
                return marcaProduto.get();
            } else {
                throw new ObjectNotFoundException();
            }
        } catch (Exception e) {
            throw new LoadFailureException();
        }
    }

    public void update(MarcaProduto marcaProduto) throws ObjectNotFoundException {
        if (marcaProdutoRepository.findById(marcaProduto.getId()).isPresent()) {
            try {
                marcaProdutoRepository.save(marcaProduto);
            } catch (Exception e) {
                throw new SaveFailureException();
            }
        } else {
            throw new ObjectNotFoundException();
        }
    }

    public void delete(MarcaProduto marcaProduto) throws DeleteFailureException {
        try {
            marcaProdutoRepository.delete(marcaProduto);
        } catch (Exception e) {
            throw new DeleteFailureException();
        }
    }
}
