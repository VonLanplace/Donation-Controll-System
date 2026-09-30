package com.vonlanplace.doacao.web;

import com.vonlanplace.doacao.entrevistador.Entrevistador;
import com.vonlanplace.doacao.entrevistador.EntrevistadorMapper;
import com.vonlanplace.doacao.entrevistador.EntrevistadorRepository;
import com.vonlanplace.doacao.entrevistador.EntrevistadorResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Valida credenciais de login contra o hash BCrypt do entrevistador.
 *
 * <p><strong>O único papel aqui é separar "encontrei" de "as credenciais
 * conferem".</strong> {@code EntrevistadorService.create} grava o hash; este
 * serviço compara a senha em claro do formulário com o hash gravado, usando o
 * mesmo {@link PasswordEncoder} do cadastro. O hash nunca sai deste pacote e
 * nunca vira resposta.</p>
 *
 * <p><strong>O caso de "não encontrado" vira a mesma exceção de "senha
 * errada".</strong> Consultar por e-mail para dar mensagem diferente quando o
 * e-mail não existe transformaria o login em enumerador de contas.</p>
 */
@Service
public class AutenticacaoService {

    private final EntrevistadorRepository entrevistadorRepository;
    private final EntrevistadorMapper entrevistadorMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construtor com injeção de dependências.
     *
     * @param entrevistadorRepository repositório de entrevistadores
     * @param entrevistadorMapper     mapper MapStruct, para expor o DTO sem hash
     * @param passwordEncoder         codificador BCrypt, exposto por {@code PasswordConfig}
     */
    public AutenticacaoService(
            EntrevistadorRepository entrevistadorRepository,
            EntrevistadorMapper entrevistadorMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.entrevistadorRepository = entrevistadorRepository;
        this.entrevistadorMapper = entrevistadorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica pelo e-mail e senha.
     *
     * @param email e-mail de acesso
     * @param senha senha em claro, vinda do formulário
     * @return DTO do entrevistador autenticado, sem o hash
     * @throws CredenciaisInvalidasException se o e-mail não existe ou a senha não confere
     */
    @Transactional(readOnly = true)
    public EntrevistadorResponseDTO autenticar(String email, String senha) throws CredenciaisInvalidasException {
        Entrevistador entrevistador = entrevistadorRepository.findByEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(senha, entrevistador.getSenhaHash())) {
            throw new CredenciaisInvalidasException();
        }

        return entrevistadorMapper.toResponseDTO(entrevistador);
    }
}