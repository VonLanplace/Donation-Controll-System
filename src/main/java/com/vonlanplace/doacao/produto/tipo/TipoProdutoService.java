package com.vonlanplace.doacao.produto.tipo;

import com.vonlanplace.doacao.produto.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio de {@link TipoProduto}.
 *
 * <p>Espelha {@code MarcaProdutoService} exatamente: identificação por nome, nenhuma
 * operação por {@code id}, e conflitos sinalizados com {@link IllegalStateException} para
 * virarem HTTP 409.</p>
 */
@Service
public class TipoProdutoService {

    private final TipoProdutoRepository tipoProdutoRepository;
    private final TipoProdutoMapper tipoProdutoMapper;
    private final ProdutoRepository produtoRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param tipoProdutoRepository repositório de tipos
     * @param tipoProdutoMapper     conversor entidade/DTO
     * @param produtoRepository     repositório de produtos, usado na trava de exclusão
     */
    public TipoProdutoService(
            TipoProdutoRepository tipoProdutoRepository,
            TipoProdutoMapper tipoProdutoMapper,
            ProdutoRepository produtoRepository
    ) {
        this.tipoProdutoRepository = tipoProdutoRepository;
        this.tipoProdutoMapper = tipoProdutoMapper;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Busca o tipo pelo nome.
     *
     * @param nome nome do tipo
     * @return DTO de resposta do tipo
     * @throws EntityNotFoundException se nenhum tipo tiver esse nome
     */
    @Transactional(readOnly = true)
    public TipoProdutoResponseDTO findByNome(String nome) throws EntityNotFoundException {
        return tipoProdutoMapper.toResponseDTO(
                tipoProdutoRepository.findByNome(nome)
                        .orElseThrow(() -> new EntityNotFoundException("Tipo Produto not found")));
    }

    /**
     * Lista os tipos, paginados.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<TipoProdutoResponseDTO> findAll(Pageable pageable) {
        return tipoProdutoRepository.findAll(pageable)
                .map(tipoProdutoMapper::toResponseDTO);
    }

    /**
     * Lista os nomes de todos os tipos, sem paginação.
     *
     * <p>Alimenta o {@code <select>} do formulário de produto, que precisa do
     * conjunto inteiro. Reaproveitar o {@code findAll(Pageable)} truncaria em
     * silêncio a partir da centésima primeira entrada, por causa do
     * {@code max-page-size=100}, e o usuário veria um tipo que existe como se
     * não existisse.</p>
     *
     * @return lista de nomes de tipo em ordem alfabética
     */
    @Transactional(readOnly = true)
    public List<String> listaNomes() {
        return tipoProdutoRepository.findAll()
                .stream()
                .map(TipoProduto::getNome)
                .sorted()
                .toList();
    }

    /**
     * Cria um tipo, recusando nomes já cadastrados.
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta do tipo criado
     * @throws IllegalStateException se o nome já estiver em uso
     */
    @Transactional
    public TipoProdutoResponseDTO create(TipoProdutoCreateDTO createDTO) {
        if (tipoProdutoRepository.findByNome(createDTO.nome()).isPresent()) {
            throw new IllegalStateException("Nome já em uso por outro tipo de produto");
        }
        TipoProduto tipoProdutoFromDto = tipoProdutoMapper.toEntity(createDTO);
        TipoProduto tipoProdutoSaved = tipoProdutoRepository.save(tipoProdutoFromDto);
        return tipoProdutoMapper.toResponseDTO(tipoProdutoSaved);
    }

    /**
     * Renomeia o tipo identificado pelo nome atual.
     *
     * @param nomeAtual nome que identifica o tipo a alterar
     * @param updateDTO dados de entrada, contendo o novo nome
     * @return DTO de resposta do tipo atualizado
     * @throws EntityNotFoundException se o tipo identificado por {@code nomeAtual} não existir
     * @throws IllegalStateException    se o novo nome pertencer a outro tipo
     */
    @Transactional
    public TipoProdutoResponseDTO update(
            String nomeAtual,
            TipoProdutoUpdateDTO updateDTO
    ) throws EntityNotFoundException {
        TipoProduto tipoProduto = tipoProdutoRepository.findByNome(nomeAtual)
                .orElseThrow(() -> new EntityNotFoundException("Tipo Produto not found"));

        tipoProdutoRepository.findByNome(updateDTO.nome())
                .filter(existing -> !existing.getId().equals(tipoProduto.getId()))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Nome já em uso por outro tipo de produto");
                });

        tipoProdutoMapper.updateEntityFromDTO(updateDTO, tipoProduto);
        TipoProduto tipoProdutoSaved = tipoProdutoRepository.save(tipoProduto);
        return tipoProdutoMapper.toResponseDTO(tipoProdutoSaved);
    }

    /**
     * Exclui o tipo pelo nome, recusando a remoção quando há produtos vinculados.
     *
     * @param nome nome do tipo
     * @throws EntityNotFoundException se o tipo não existir
     * @throws IllegalStateException    se algum produto ainda referencia o tipo
     */
    @Transactional
    public void delete(String nome) throws EntityNotFoundException {
        TipoProduto tipoProduto = tipoProdutoRepository.findByNome(nome)
                .orElseThrow(() -> new EntityNotFoundException("Tipo Produto not found"));

        if (produtoRepository.existsByTipoProduto(tipoProduto)) {
            throw new IllegalStateException("Não é possível excluir tipo com produtos vinculados");
        }

        tipoProdutoRepository.delete(tipoProduto);
    }
}
