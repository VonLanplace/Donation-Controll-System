package com.vonlanplace.doacao.doacao;

import com.vonlanplace.doacao.entrevistador.Entrevistador;
import com.vonlanplace.doacao.entrevistador.EntrevistadorRepository;
import com.vonlanplace.doacao.produto.Produto;
import com.vonlanplace.doacao.produto.ProdutoRepository;
import com.vonlanplace.doacao.recebedor.Recebedor;
import com.vonlanplace.doacao.recebedor.RecebedorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Regras de negócio de {@link Doacao}.
 *
 * <p><strong>A escrita da entidade é feita à mão, sem MapStruct.</strong> Existe apenas
 * o sentido de leitura ({@code DoacaoMapper}). Montar a lista de itens exige, para cada
 * um, resolver o {@code Produto} e apontar a referência de volta para a {@code Doacao} —
 * os dois lados da chave composta de {@code DoacaoProduto}. MapStruct não faz isso sem
 * código gerado intermediário, e um link errado só apareceria como chave nula em tempo
 * de execução.</p>
 *
 * <p><strong>Produto repetido na mesma lista é erro explícito.</strong> {@link #buildProdutos}
 * mantém um {@code Set} de ids já vistos e recusa a segunda ocorrência com
 * {@link IllegalStateException}, que vira 409. Sem essa checagem o banco recusaria com
 * violação da chave composta, e o cliente receberia a mensagem genérica de conflito.</p>
 *
 * <p><strong>Atualização substitui a lista inteira, e o {@code flush} é obrigatório.</strong>
 * Ver {@link #replaceProdutos(Doacao, List)}: sem o flush entre {@code deleteAll} e a
 * inserção dos novos itens, o Hibernate tentaria inserir a linha nova antes de a antiga
 * ter sumido e a operação falharia por chave duplicada.</p>
 */
@Service
public class DoacaoService {

    final private DoacaoRepository doacaoRepository;
    final private DoacaoMapper doacaoMapper;
    final private DoacaoProdutoRepository doacaoProdutoRepository;
    final private RecebedorRepository recebedorRepository;
    final private EntrevistadorRepository entrevistadorRepository;
    final private ProdutoRepository produtoRepository;

    /**
     * Construtor com injeção de dependências.
     *
     * @param doacaoRepository         repositório de doações
     * @param doacaoMapper             conversor entidade/DTO
     * @param doacaoProdutoRepository  repositório de itens, usado na troca de lista
     * @param recebedorRepository      repositório de recebedores
     * @param entrevistadorRepository  repositório de entrevistadores
     * @param produtoRepository        repositório de produtos
     */
    public DoacaoService(
            DoacaoRepository doacaoRepository,
            DoacaoMapper doacaoMapper,
            DoacaoProdutoRepository doacaoProdutoRepository,
            RecebedorRepository recebedorRepository,
            EntrevistadorRepository entrevistadorRepository,
            ProdutoRepository produtoRepository
    ) {
        this.doacaoRepository = doacaoRepository;
        this.doacaoMapper = doacaoMapper;
        this.doacaoProdutoRepository = doacaoProdutoRepository;
        this.recebedorRepository = recebedorRepository;
        this.entrevistadorRepository = entrevistadorRepository;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Resolve o recebedor, tolerando ausência de vínculo.
     *
     * @param recebedorId identificador informado, pode ser {@code null}
     * @return o recebedor, ou {@code null} se o id for {@code null}
     * @throws EntityNotFoundException se o id for preenchido e não existir
     */
    private Recebedor resolveRecebedor(UUID recebedorId) {
        if (recebedorId == null) return null;
        return recebedorRepository.findById(recebedorId)
                .orElseThrow(() -> new EntityNotFoundException("Recebedor not found: " + recebedorId));
    }

    /**
     * Resolve o entrevistador, tolerando ausência de vínculo.
     *
     * @param entrevistadorId identificador informado, pode ser {@code null}
     * @return o entrevistador, ou {@code null} se o id for {@code null}
     * @throws EntityNotFoundException se o id for preenchido e não existir
     */
    private Entrevistador resolveEntrevistador(UUID entrevistadorId) {
        if (entrevistadorId == null) return null;
        return entrevistadorRepository.findById(entrevistadorId)
                .orElseThrow(() -> new EntityNotFoundException("Entrevistador not found: " + entrevistadorId));
    }

    /**
     * Monta a lista de itens de uma doação, resolvendo cada produto e recusando
     * repetição dentro da mesma lista.
     *
    /**
     * Monta a lista de itens de uma doação, resolvendo cada produto e recusando
     * repetição dentro da mesma lista.
     *
     * <p>Lista {@code null} produz lista vazia, e não erro: uma doação sem itens é
     * válida. Lista vazia é o mesmo caso.</p>
     *
     *
     * @param doacao doação dona dos itens, já com id quando vem de uma atualização
     * @param itens  itens descritos no corpo da requisição, pode ser {@code null}
     * @return lista de entidades de junção com os dois lados ligados
     * @throws EntityNotFoundException    se algum produto não existir
     * @throws IllegalStateException      se o mesmo produto aparecer mais de uma vez
     */
    private List<DoacaoProduto> buildProdutos(
            Doacao doacao,
            List<DoacaoProdutoCreateDTO> itens
    ) {
        List<DoacaoProduto> produtos = new ArrayList<>();
        if (itens == null) return produtos;

        Set<UUID> vistos = new HashSet<>();
        for (DoacaoProdutoCreateDTO item : itens) {
            if (!vistos.add(item.produtoId())) {
                throw new IllegalStateException("Produto duplicado na doação: " + item.produtoId());
            }
            Produto produto = produtoRepository.findById(item.produtoId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto not found: " + item.produtoId()));

            DoacaoProduto doacaoProduto = new DoacaoProduto();
            doacaoProduto.setDoacao(doacao);
            doacaoProduto.setProduto(produto);
            doacaoProduto.setQuantidade(item.quantidade());
            doacaoProduto.setDataValidade(item.dataValidade());
            produtos.add(doacaoProduto);
        }
        return produtos;
    }

    /**
     * Troca a lista de itens da doação pela lista descrita no corpo da requisição.
     *
     * <p><strong>O {@code flush} entre {@code deleteAll} e a inserção não é
     * decoração.</strong> Sem ele, o Hibernate emite os deletes e os inserts na ordem em
     * que foram enfileirados, e a inserção da linha nova acontece antes de a antiga
     * deixar o banco — falha por chave composta duplicada. O {@code flush} força a saída
     * dos deletes antes de os itens novos entrarem na fila.</p>
     *
     * <p>Uma consequência da estratégia: reenviar a mesma lista com os mesmos produtos
     * apaga e reinsere as linhas, ainda que nada tenha mudado de fato.</p>
     *
     * @param doacao doação a alterar, já carregada e gerenciada
     * @param itens  lista final de itens, pode ser {@code null} ou vazia
     * @throws EntityNotFoundException se algum produto não existir
     * @throws IllegalStateException   se houver produto repetido na lista
     */
    private void replaceProdutos(Doacao doacao, List<DoacaoProdutoCreateDTO> itens) {
        List<DoacaoProduto> existentes = new ArrayList<>(doacao.getProdutos());
        if (!existentes.isEmpty()) {
            doacaoProdutoRepository.deleteAll(existentes);
            doacaoProdutoRepository.flush();
            doacao.getProdutos().clear();
        }
        doacao.getProdutos().addAll(buildProdutos(doacao, itens));
    }

    /**
     * Busca a doação pelo identificador.
     *
     * @param id identificador da doação
     * @return DTO de resposta
     * @throws EntityNotFoundException se a doação não existir
     */
    @Transactional(readOnly = true)
    public DoacaoResponseDTO findById(UUID id) throws EntityNotFoundException {
        return doacaoMapper.toResponseDTO(
                doacaoRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Doacao not found: " + id)));
    }

    /**
     * Lista todas as doações, paginadas.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<DoacaoResponseDTO> findAll(Pageable pageable) {
        return doacaoRepository.findAll(pageable)
                .map(doacaoMapper::toResponseDTO);
    }

    /**
     * Registra uma doação nova com a lista de itens informada.
     *
     * @param createDTO dados de entrada
     * @return DTO de resposta da doação criada
     * @throws EntityNotFoundException se algum recebedor, entrevistador ou produto não existir
     * @throws IllegalStateException   se houver produto repetido na lista de itens
     */
    @Transactional
    public DoacaoResponseDTO create(DoacaoCreateDTO createDTO) throws EntityNotFoundException {
        Doacao doacao = new Doacao();
        doacao.setData(createDTO.data());
        doacao.setNomeDoador(createDTO.nomeDoador());
        doacao.setRecebedor(resolveRecebedor(createDTO.recebedorId()));
        doacao.setEntrevistador(resolveEntrevistador(createDTO.entrevistadorId()));
        doacao.getProdutos().addAll(buildProdutos(doacao, createDTO.produtos()));

        Doacao saved = doacaoRepository.save(doacao);
        return doacaoMapper.toResponseDTO(saved);
    }

    /**
     * Substitui os dados da doação e a lista inteira de itens. Lista vazia deixa a
     * doação sem itens.
     *
     * @param id        identificador da doação
     * @param updateDTO dados de entrada
     * @return DTO de resposta da doação atualizada
     * @throws EntityNotFoundException se a doação, algum vínculo ou algum produto não existir
     * @throws IllegalStateException   se houver produto repetido na lista de itens
     */
    @Transactional
    public DoacaoResponseDTO update(UUID id, DoacaoUpdateDTO updateDTO) throws EntityNotFoundException {
        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doacao not found: " + id));

        doacao.setData(updateDTO.data());
        doacao.setNomeDoador(updateDTO.nomeDoador());
        doacao.setRecebedor(resolveRecebedor(updateDTO.recebedorId()));
        doacao.setEntrevistador(resolveEntrevistador(updateDTO.entrevistadorId()));
        replaceProdutos(doacao, updateDTO.produtos());

        Doacao saved = doacaoRepository.save(doacao);
        return doacaoMapper.toResponseDTO(saved);
    }

    /**
     * Exclui a doação. Os itens saem junto, pelo {@code cascade} e
     * {@code orphanRemoval} de {@code Doacao.produtos}.
     *
     * @param id identificador da doação
     * @throws EntityNotFoundException se a doação não existir
     */
    @Transactional
    public void delete(UUID id) throws EntityNotFoundException {
        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Doacao not found: " + id));
        doacaoRepository.delete(doacao);
    }
}
