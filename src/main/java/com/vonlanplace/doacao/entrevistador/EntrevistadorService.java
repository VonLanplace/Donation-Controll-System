package com.vonlanplace.doacao.entrevistador;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de entrevistadores.
 *
 * <p><strong>É o único lugar do projeto que toca em senha.</strong> O
 * {@code EntrevistadorCreateDTO} e o {@code EntrevistadorUpdateDTO} recebem a
 * senha em claro porque é o que o formulário envia, e o mapper de entidade
 * ignora esse campo. A conversão para BCrypt acontece aqui, e em nenhum outro
 * lugar: um {@code encode(...)} dentro do controller colocaria a dependência de
 * segurança em onze arquivos em vez de um, e um {@code encode(...)} dentro do
 * mapper esconderia o custo computacional do BCrypt de uma operação que parece
 * um simples atribuição.</p>
 *
 * <p><strong>Na edição, senha vazia é "não mexer".</strong> Sem isso, salvar
 * o nome de alguém apagaria a senha da pessoa, e o efeito apareceria só no
 * próximo login.</p>
 */
@Service
public class EntrevistadorService {

    private final EntrevistadorRepository entrevistadorRepository;
    private final EntrevistadorMapper entrevistadorMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construtor com injeção de dependências.
     *
     * @param entrevistadorRepository repositório de entrevistadores
     * @param entrevistadorMapper     mapper MapStruct
     * @param passwordEncoder         codificador BCrypt, exposto por {@code PasswordConfig}
     */
    public EntrevistadorService(
            EntrevistadorRepository entrevistadorRepository,
            EntrevistadorMapper entrevistadorMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.entrevistadorRepository = entrevistadorRepository;
        this.entrevistadorMapper = entrevistadorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Busca um entrevistador pelo id.
     *
     * @param id identificador do entrevistador
     * @return DTO de resposta, sem a senha
     * @throws EntityNotFoundException se não existir
     */
    @Transactional(readOnly = true)
    public EntrevistadorResponseDTO findById(UUID id) throws EntityNotFoundException {
        Entrevistador entrevistador = entrevistadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrevistador not found: " + id));
        return entrevistadorMapper.toResponseDTO(entrevistador);
    }

    /**
     * Busca um entrevistador pelo e-mail, que é a chave de login.
     *
     * @param email e-mail de acesso
     * @return DTO de resposta
     * @throws EntityNotFoundException se não existir
     */
    @Transactional(readOnly = true)
    public EntrevistadorResponseDTO findByEmail(String email) throws EntityNotFoundException {
        Entrevistador entrevistador = entrevistadorRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Entrevistador not found: " + email));
        return entrevistadorMapper.toResponseDTO(entrevistador);
    }

    /**
     * Lista os entrevistadores, paginados.
     *
     * @param pageable paginação e ordenação
     * @return página de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public Page<EntrevistadorResponseDTO> findAll(Pageable pageable) {
        return entrevistadorRepository.findAll(pageable)
                .map(entrevistadorMapper::toResponseDTO);
    }

    /**
     * Lista todos os entrevistadores, sem paginação, para o select da doação.
     *
     * @return lista de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public List<EntrevistadorResponseDTO> findAll() {
        return entrevistadorRepository.findAll()
                .stream()
                .map(entrevistadorMapper::toResponseDTO)
                .toList();
    }

    /**
     * Cadastra um entrevistador, codificando a senha.
     *
     * @param createDTO dados de entrada, com a senha em claro
     * @return DTO de resposta da pessoa criada
     * @throws IllegalStateException se o e-mail já estiver em uso
     */
    @Transactional
    public EntrevistadorResponseDTO create(EntrevistadorCreateDTO createDTO) {
        if (entrevistadorRepository.findByEmail(createDTO.email()).isPresent()) {
            throw new IllegalStateException("E-mail já em uso por outro entrevistador");
        }

        Entrevistador entrevistador = entrevistadorMapper.toEntity(createDTO);
        entrevistador.setSenhaHash(passwordEncoder.encode(createDTO.senha()));
        return entrevistadorMapper.toResponseDTO(entrevistadorRepository.save(entrevistador));
    }

    /**
     * Atualiza um entrevistador.
     *
     * <p>Senha só é tocada se o DTO trouxer uma. Um campo vazio no formulário
     * de edição significa "não mexer na senha", e não "a senha agora é
     * vazia".</p>
     *
     * @param id        identificador do entrevistador
     * @param updateDTO dados de entrada
     * @return DTO de resposta
     * @throws EntityNotFoundException se o entrevistador não existir
     * @throws IllegalStateException    se o e-mail pertence a outro entrevistador
     */
    @Transactional
    public EntrevistadorResponseDTO update(UUID id, EntrevistadorUpdateDTO updateDTO) {
        Entrevistador entrevistador = entrevistadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrevistador not found: " + id));

        entrevistadorRepository.findByEmail(updateDTO.email())
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new IllegalStateException("E-mail já em uso por outro entrevistador");
                });

        entrevistadorMapper.updateEntityFromDTO(updateDTO, entrevistador);
        if (updateDTO.senha() != null && !updateDTO.senha().isBlank()) {
            entrevistador.setSenhaHash(passwordEncoder.encode(updateDTO.senha()));
        }
        return entrevistadorMapper.toResponseDTO(entrevistadorRepository.save(entrevistador));
    }

    /**
     * Apaga um entrevistador.
     *
     * @param id identificador do entrevistador
     * @throws EntityNotFoundException se o entrevistador não existir
     */
    @Transactional
    public void delete(UUID id) throws EntityNotFoundException {
        Entrevistador entrevistador = entrevistadorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrevistador not found: " + id));
        entrevistadorRepository.delete(entrevistador);
    }
}
