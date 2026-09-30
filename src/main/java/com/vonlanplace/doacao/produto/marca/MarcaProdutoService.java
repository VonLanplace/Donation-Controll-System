package com.vonlanplace.doacao.produto.marca;

import com.vonlanplace.doacao.produto.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras de negócio de {@link MarcaProduto}.
 *
 * <p><strong>Identificação por nome.</strong> Não há overload de método por {@code id}
 * em nenhum ponto deste serviço: busca, atualização e exclusão recebem o nome. É por
 * isso que a atualização recebe o nome atual e o novo separadamente.</p>
 *
 * <p><strong>Conflitos usam {@link IllegalStateException}, não
 * {@link EntityNotFoundException}.</strong> O {@code GlobalExceptionHandler} traduz a
 * primeira para HTTP 409 e a segunda para 404, o que distingue "não existe" de "já
 * pertence a outro registro".</p>
 */
@Service
public class MarcaProdutoService {

    final private MarcaProdutoRepository marcaProdutoRepository;
    final private MarcaProdutoMapper marcaProdutoMapper;
    final private ProdutoRepository produtoRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param marcaProdutoRepository repositório de marcas
     * @param marcaProdutoMapper      conversor entidade/DTO
     * @param produtoRepository       repositório de produtos, usado na trava de exclusão
     */
    public MarcaProdutoService(
            MarcaProdutoRepository marcaProdutoRepository,
            MarcaProdutoMapper marcaProdutoMapper,
            ProdutoRepository produtoRepository
    ) {
        this.marcaProdutoRepository = marcaProdutoRepository;
        this.marcaProdutoMapper = marcaProdutoMapper;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Busca a marca pelo nome.
     *
     * @param nome nome da marca
     * @return DTO de resposta da marca
     * @throws EntityNotFoundException se nenhuma marca tiver esse nome
     */
    @Transactional(readOnly = true)
    public MarcaProdutoResponseDTO findByNome(String nome) throws EntityNotFoundException {
        MarcaProduto marcaProduto = marcaProdutoRepository.findByNome(nome)
                .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found"));
        return marcaProdutoMapper.toResponseDTO(marcaProduto);
    }

    /**
     * Lista as marcas, paginadas.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<MarcaProdutoResponseDTO> findAll(Pageable pageable) {
        return marcaProdutoRepository.findAll(pageable)
                .map(marcaProdutoMapper::toResponseDTO);
    }

    /**
     * Lista os nomes de todas as marcas, sem paginação.
     *
     * <p>Existe para alimentar o {@code <select>} do formulário de produto, que
     * precisa do conjunto inteiro. Passar pelo {@code findAll(Pageable)} seria um
     * erro silencioso: com {@code max-page-size=100}, a partir da centésima
     * primeira marca o formulário deixaria de oferecer as que sobraram, sem
     * nenhuma mensagem, e o usuário concluiria que a marca não existe.</p>
     *
     * @return lista de nomes de marca em ordem alfabética
     */
    @Transactional(readOnly = true)
    public List<String> listaNomes() {
        return marcaProdutoRepository.findAll()
                .stream()
                .map(MarcaProduto::getNome)
                .sorted()
                .toList();
    }

    /**
     * Cria uma marca, recusando nomes já cadastrados.
     *
     * <p>A checagem é feita aqui e não apenas pelo índice único do banco, para que o
     * cliente receba 409 em vez de uma violação de constraint traduzida pelo handler
     * genérico.</p>
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta da marca criada
     * @throws IllegalStateException se o nome já estiver em uso
     */
    @Transactional
    public MarcaProdutoResponseDTO create(MarcaProdutoCreateDTO createDTO) {
        if (marcaProdutoRepository.findByNome(createDTO.nome()).isPresent()) {
            throw new IllegalStateException("Nome já em uso por outra marca");
        }
        MarcaProduto marcaProduto = marcaProdutoMapper.toEntity(createDTO);
        MarcaProduto marcaProdutoSaved = marcaProdutoRepository.save(marcaProduto);
        return marcaProdutoMapper.toResponseDTO(marcaProdutoSaved);
    }

    /**
     * Renomeia a marca identificada pelo nome atual.
     *
     * @param nomeAtual nome que identifica a marca a alterar
     * @param updateDTO dados de entrada, contendo o novo nome
     * @return DTO de resposta da marca atualizada
     * @throws EntityNotFoundException se a marca identificada por {@code nomeAtual} não existir
     * @throws IllegalStateException    se o novo nome pertencer a outra marca
     */
    @Transactional
    public MarcaProdutoResponseDTO update(
            String nomeAtual,
            MarcaProdutoUpdateDTO updateDTO
    ) throws EntityNotFoundException {
        MarcaProduto marcaProduto = marcaProdutoRepository.findByNome(nomeAtual)
                .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found"));

        marcaProdutoRepository.findByNome(updateDTO.nome())
                .filter(existing -> !existing.getId().equals(marcaProduto.getId()))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Nome já em uso por outra marca");
                });

        marcaProdutoMapper.updateEntityFromDTO(updateDTO, marcaProduto);
        MarcaProduto marcaProdutoSaved = marcaProdutoRepository.save(marcaProduto);
        return marcaProdutoMapper.toResponseDTO(marcaProdutoSaved);
    }

    /**
     * Exclui a marca pelo nome, recusando a remoção quando há produtos vinculados.
     *
     * @param nome nome da marca
     * @throws EntityNotFoundException se a marca não existir
     * @throws IllegalStateException    se algum produto ainda referencia a marca
     */
    @Transactional
    public void delete(String nome) throws EntityNotFoundException {
        MarcaProduto marcaProduto = marcaProdutoRepository.findByNome(nome)
                .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found"));

        if (produtoRepository.existsByMarcaProduto(marcaProduto)) {
            throw new IllegalStateException("Não é possível excluir marca com produtos vinculados");
        }

        marcaProdutoRepository.delete(marcaProduto);
    }
}
