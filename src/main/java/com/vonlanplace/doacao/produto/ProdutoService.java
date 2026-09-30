package com.vonlanplace.doacao.produto;

import com.vonlanplace.doacao.produto.marca.MarcaProduto;
import com.vonlanplace.doacao.produto.marca.MarcaProdutoRepository;
import com.vonlanplace.doacao.produto.tipo.TipoProduto;
import com.vonlanplace.doacao.produto.tipo.TipoProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Regras de negócio de {@link Produto}.
 *
 * <p><strong>Assimetria de chaves:</strong> o produto é identificado por {@code UUID}, mas
 * sua marca e seu tipo são resolvidos pelo {@code nome}. Por isso os DTOs de entrada
 * carregam {@code String marcaProduto} e {@code String tipoProduto}, e por isso as
 * consultas por marca ou tipo recebem o nome, não o id.</p>
 *
 * <p>Nenhum método desta classe devolve entidade: todos retornam
 * {@link ProdutoResponseDTO}, para manter o contrato de saída estável e o aninhamento
 * de marca e tipo no formato sem {@code id}.</p>
 */
@Service
public class ProdutoService {

    final private ProdutoRepository produtoRepository;
    final private ProdutoMapper produtoMapper;
    final private MarcaProdutoRepository marcaProdutoRepository;
    final private TipoProdutoRepository tipoProdutoRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param produtoRepository      repositório de produtos
     * @param produtoMapper          conversor entidade/DTO
     * @param marcaProdutoRepository repositório de marcas, usado para resolver a marca
     *                               pelo nome informado no DTO
     * @param tipoProdutoRepository  repositório de tipos, usado para resolver o tipo
     *                               pelo nome informado no DTO
     */
    public ProdutoService(
            ProdutoRepository produtoRepository,
            ProdutoMapper produtoMapper,
            MarcaProdutoRepository marcaProdutoRepository,
            TipoProdutoRepository tipoProdutoRepository
    ) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
        this.marcaProdutoRepository = marcaProdutoRepository;
        this.tipoProdutoRepository = tipoProdutoRepository;
    }

    /**
     * Resolve a marca pelo nome, que é a chave de negócio da entidade.
     *
     * @param nome nome da marca
     * @return entidade da marca
     * @throws EntityNotFoundException se nenhuma marca tiver esse nome
     */
    private MarcaProduto resolveMarcaProduto(String nome) {
        return marcaProdutoRepository.findByNome(nome)
                .orElseThrow(() -> new EntityNotFoundException("Marca Produto not found: " + nome));
    }

    /**
     * Resolve o tipo pelo nome, que é a chave de negócio da entidade.
     *
     * @param nome nome do tipo
     * @return entidade do tipo
     * @throws EntityNotFoundException se nenhum tipo tiver esse nome
     */
    private TipoProduto resolveTipoProduto(String nome) {
        return tipoProdutoRepository.findByNome(nome)
                .orElseThrow(() -> new EntityNotFoundException("Tipo Produto not found: " + nome));
    }

    /**
     * Busca um produto pelo identificador.
     *
     * @param id identificador do produto
     * @return DTO de resposta do produto
     * @throws EntityNotFoundException se o produto não existir
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO findById(UUID id) throws EntityNotFoundException {
        return produtoMapper.toResponseDTO(
                produtoRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Produto not found")));
    }

    /**
     * Busca produtos pelo código de barras.
     *
     * @param codigoBarras código de barras procurado
     * @param pageable     configuração de paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> findByCodigoBarras(
            String codigoBarras,
            Pageable pageable
    ) {
        return produtoRepository.findByCodigoBarras(codigoBarras, pageable)
                .map(produtoMapper::toResponseDTO);
    }

    /**
     * Busca produtos de uma marca, informada pelo nome.
     *
     * @param marcaProduto nome da marca
     * @param pageable     configuração de paginação e ordenação
     * @return página de DTOs de resposta
     * @throws EntityNotFoundException se a marca não existir
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> findByMarcaProduto(
            String marcaProduto,
            Pageable pageable
    ) throws EntityNotFoundException {
        return produtoRepository.findAllByMarcaProduto(resolveMarcaProduto(marcaProduto), pageable)
                .map(produtoMapper::toResponseDTO);
    }

    /**
     * Busca produtos de um tipo, informado pelo nome.
     *
     * @param tipoProduto nome do tipo
     * @param pageable    configuração de paginação e ordenação
     * @return página de DTOs de resposta
     * @throws EntityNotFoundException se o tipo não existir
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> findByTipoProduto(
            String tipoProduto,
            Pageable pageable
    ) throws EntityNotFoundException {
        return produtoRepository.findAllByTipoProduto(resolveTipoProduto(tipoProduto), pageable)
                .map(produtoMapper::toResponseDTO);
    }

    /**
     * Lista todos os produtos, paginados.
     *
     * @param pageable configuração de paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> findAll(Pageable pageable) {
        return produtoRepository.findAll(pageable)
                .map(produtoMapper::toResponseDTO);
    }

    /**
     * Lista todo o catálogo de produtos, sem paginação, ordenado pelo código de
     * barras.
     *
     * <p><strong>Não paginado de propósito.</strong> O select da doação
     * precisa do catálogo inteiro, e o {@code max-page-size=100} do
     * {@code application.properties} cortaria a lista em silêncio se
     * passássemos um {@code Pageable} — quem só olhasse a primeira página do
     * select não notaria que produtos sumiram. Mesmo raciocínio de
     * {@code ResidenciaService.findAll()}, {@code RecebedorService.findAll()} e
     * {@code EntrevistadorService.findAll()}.</p>
     *
     * @return lista de produtos ordenada pelo código de barras
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> findAll() {
        return produtoRepository.findAll(Sort.by("codigoBarras")).stream()
                .map(produtoMapper::toResponseDTO)
                .toList();
    }

    /**
     * Cria um produto resolvendo marca e tipo pelos nomes informados.
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta do produto criado
     * @throws EntityNotFoundException se a marca ou o tipo informados não existirem
     */
    @Transactional
    public ProdutoResponseDTO create(ProdutoCreateDTO createDTO) throws EntityNotFoundException {
        Produto produto = produtoMapper.toEntity(createDTO);
        produto.setMarcaProduto(resolveMarcaProduto(createDTO.marcaProduto()));
        produto.setTipoProduto(resolveTipoProduto(createDTO.tipoProduto()));
        Produto produtoSaved = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSaved);
    }

    /**
     * Atualiza um produto existente. As relações são resolvidas <em>antes</em> da
     * aplicação do DTO porque {@link ProdutoMapper} as ignora, o que impede que a
     * atualização apague marca ou tipo.
     *
     * @param id        identificador do produto
     * @param updateDTO dados de entrada
     * @return DTO de resposta do produto atualizado
     * @throws EntityNotFoundException se o produto, a marca ou o tipo não existirem
     */
    @Transactional
    public ProdutoResponseDTO update(UUID id, ProdutoUpdateDTO updateDTO) throws EntityNotFoundException {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto not found"));
        produto.setMarcaProduto(resolveMarcaProduto(updateDTO.marcaProduto()));
        produto.setTipoProduto(resolveTipoProduto(updateDTO.tipoProduto()));
        produtoMapper.updateEntityFromDTO(updateDTO, produto);
        Produto produtoSaved = produtoRepository.save(produto);
        return produtoMapper.toResponseDTO(produtoSaved);
    }

    /**
     * Remove um produto pelo identificador.
     *
     * @param produtoId identificador do produto
     * @throws EntityNotFoundException se o produto não existir
     */
    @Transactional
    public void delete(UUID produtoId) throws EntityNotFoundException {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto not found"));
        produtoRepository.delete(produto);
    }
}
