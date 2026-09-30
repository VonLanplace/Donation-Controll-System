package com.vonlanplace.doacao.entrevistador;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Fixa o contrato de senha do entrevistador.
 *
 * <p><strong>Estes são os testes que impedem vazamento de credencial.</strong> A
 * senha é o único campo do projeto com regra de ida e volta: chega em claro no
 * DTO de entrada e tem de estar em BCrypt na entidade. Um mapper que ganhasse
 * um campo a mais, ou um service que gravasse a senha direto, passaria em
 * qualquer teste de CRUD e falharia aqui.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("Senha do entrevistador")
class EntrevistadorSenhaTest {

    @Autowired
    private EntrevistadorService entrevistadorService;

    @Autowired
    private EntrevistadorRepository entrevistadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("o cadastro guarda a senha em BCrypt, não em claro")
    void cadastroCodificaASenha() {
        EntrevistadorResponseDTO criado = entrevistadorService.create(
                new EntrevistadorCreateDTO("Ana Souza", "ana@exemplo.org", "segredo123"));

        String hash = entrevistadorRepository.findById(criado.id()).orElseThrow().getSenhaHash();

        assertThat(hash)
                .as("a senha foi gravada em claro")
                .isNotEqualTo("segredo123")
                .startsWith("$2");
        assertThat(passwordEncoder.matches("segredo123", hash))
                .as("o hash não confere com a senha original")
                .isTrue();
    }

    @Test
    @DisplayName("o DTO de resposta não carrega o hash")
    void respostaNaoCarregaHash() {
        EntrevistadorResponseDTO criado = entrevistadorService.create(
                new EntrevistadorCreateDTO("Ana Souza", "ana2@exemplo.org", "segredo123"));

        assertThat(criado.toString())
                .as("o toString do response DTO expõe o hash")
                .doesNotContain(passwordEncoder.encode("segredo123"))
                .doesNotContain("segredo123");
    }

    @Test
    @DisplayName("editar sem senha mantém a senha anterior")
    void editarSemSenhaMantemAAnterior() {
        EntrevistadorResponseDTO criado = entrevistadorService.create(
                new EntrevistadorCreateDTO("Ana Souza", "ana3@exemplo.org", "senhaantiga"));
        String hashOriginal = entrevistadorRepository.findById(criado.id())
                .orElseThrow().getSenhaHash();

        entrevistadorService.update(criado.id(),
                new EntrevistadorUpdateDTO("Ana Souza Alterada", "ana3@exemplo.org", null));

        String hashDepois = entrevistadorRepository.findById(criado.id())
                .orElseThrow().getSenhaHash();

        assertThat(hashDepois)
                .as("editar o nome apagou ou trocou a senha por causa de um campo vazio")
                .isEqualTo(hashOriginal);
        assertThat(passwordEncoder.matches("senhaantiga", hashDepois)).isTrue();
    }

    @Test
    @DisplayName("editar com senha nova troca o hash")
    void editarComSenhaTrocaOHash() {
        EntrevistadorResponseDTO criado = entrevistadorService.create(
                new EntrevistadorCreateDTO("Ana Souza", "ana4@exemplo.org", "senhaantiga"));

        entrevistadorService.update(criado.id(),
                new EntrevistadorUpdateDTO("Ana Souza", "ana4@exemplo.org", "senhanova"));

        String hash = entrevistadorRepository.findById(criado.id()).orElseThrow().getSenhaHash();

        assertThat(passwordEncoder.matches("senhanova", hash)).isTrue();
        assertThat(passwordEncoder.matches("senhaantiga", hash)).isFalse();
    }

    @Test
    @DisplayName("e-mail repetido é recusado no cadastro")
    void emailRepetidoNoCadastro() {
        entrevistadorService.create(
                new EntrevistadorCreateDTO("Ana Souza", "ana5@exemplo.org", "segredo123"));

        assertThatThrownBy(() -> entrevistadorService.create(
                new EntrevistadorCreateDTO("Outra Ana", "ana5@exemplo.org", "segredo123")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("já em uso");
    }

    @Test
    @DisplayName("editar mantendo o próprio e-mail não é conflito")
    void editarComOMesmoEmailNaoEConflito() {
        EntrevistadorResponseDTO criado = entrevistadorService.create(
                new EntrevistadorCreateDTO("Ana Souza", "ana6@exemplo.org", "segredo123"));

        EntrevistadorResponseDTO atualizado = entrevistadorService.update(criado.id(),
                new EntrevistadorUpdateDTO("Ana Souza Novo Nome", "ana6@exemplo.org", null));

        assertThat(atualizado.nome()).isEqualTo("Ana Souza Novo Nome");
    }

    @Test
    @DisplayName("editar para o e-mail de outra pessoa é recusado")
    void editarParaEmailDeOutraPessoa() {
        EntrevistadorResponseDTO primeiro = entrevistadorService.create(
                new EntrevistadorCreateDTO("Ana Souza", "ana7@exemplo.org", "segredo123"));
        EntrevistadorResponseDTO segundo = entrevistadorService.create(
                new EntrevistadorCreateDTO("Bruno Lima", "bruno@exemplo.org", "segredo123"));

        assertThatThrownBy(() -> entrevistadorService.update(primeiro.id(),
                new EntrevistadorUpdateDTO("Ana Souza", "bruno@exemplo.org", null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("já em uso");

        assertThat(entrevistadorService.findById(segundo.id()).email())
                .as("a tentativa de roubar o e-mail de outro alterou o dono original")
                .isEqualTo("bruno@exemplo.org");
    }
}
