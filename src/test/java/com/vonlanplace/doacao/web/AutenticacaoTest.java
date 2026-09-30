package com.vonlanplace.doacao.web;

import com.vonlanplace.doacao.entrevistador.Entrevistador;
import com.vonlanplace.doacao.entrevistador.EntrevistadorRepository;
import com.vonlanplace.doacao.entrevistador.EntrevistadorResponseDTO;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Fluxo de autenticação por sessão.
 *
 * <p><strong>O que este teste prova que o build não prova.</strong> O
 * interceptor fecha as rotas web, mas só em contexto Spring ele participa das
 * chamadas do MockMvc. Um login em claro, um bootstrap que tranca o sistema ou
 * um redirect que aponta para a própria página protegida são falhas que só
 * aparecem aqui.</p>
 *
 * <p><strong>Roda transacional, como os demais testes de página.</strong> O
 * entrevistador criado para o {@code POST /login} existe só durante o teste, e
 * o {@code @BeforeEach} vazio é de propósito: cada caso monta o seu cenário.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Autenticação por sessão")
class AutenticacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntrevistadorRepository entrevistadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Persiste um entrevistador com a senha codificada em BCrypt.
     */
    private void criarEntrevistador(String email, String senha) {
        Entrevistador entrevistador = new Entrevistador();
        entrevistador.setNome("Ana");
        entrevistador.setEmail(email);
        entrevistador.setSenhaHash(passwordEncoder.encode(senha));
        entrevistadorRepository.save(entrevistador);
    }

    @Test
    @DisplayName("a página de login abre sem autenticação")
    void loginPageAbre() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/form"))
                .andExpect(content().string(containsString("Entrar")));
    }

    @Test
    @DisplayName("página protegida sem sessão redireciona para o login")
    void semSessaoRedirecionaParaLogin() throws Exception {
        mockMvc.perform(get("/recebedor/listar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("a home também é protegida")
    void homeProtegida() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("página protegida com sessão logada renderiza")
    void comSessaoListaCarrega() throws Exception {
        mockMvc.perform(get("/recebedor/listar").session(SessaoAutenticacao.logada()))
                .andExpect(status().isOk())
                .andExpect(view().name("recebedor/listar"));
    }

    @Test
    @DisplayName("login válido abre a sessão e redireciona para a home")
    void loginValidoAbreSessao() throws Exception {
        criarEntrevistador("ana@exemplo.com", "segredo1");

        MvcResult resultado = mockMvc.perform(post("/login")
                        .param("email", "ana@exemplo.com")
                        .param("senha", "segredo1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        HttpSession sessao = resultado.getRequest().getSession(false);
        assertThat(sessao).isNotNull();
        EntrevistadorResponseDTO logado =
                (EntrevistadorResponseDTO) sessao.getAttribute(LoginController.ATRIBUTO_SESSAO);
        assertThat(logado).isNotNull();
        assertThat(logado.email()).isEqualTo("ana@exemplo.com");
    }

    @Test
    @DisplayName("senha errada devolve o formulário sem abrir sessão logada")
    void senhaErrada() throws Exception {
        criarEntrevistador("ana@exemplo.com", "segredo1");

        MvcResult resultado = mockMvc.perform(post("/login")
                        .param("email", "ana@exemplo.com")
                        .param("senha", "errada"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/form"))
                .andExpect(content().string(containsString("E-mail ou senha inválidos.")))
                .andReturn();

        // A sessão pode ser criada pelo resolvedor do parâmetro HttpSession; o
        // que importa é que ela não guarda o atributo de logado.
        HttpSession sessao = resultado.getRequest().getSession(false);
        if (sessao != null) {
            assertThat(sessao.getAttribute(LoginController.ATRIBUTO_SESSAO)).isNull();
        }
    }

    @Test
    @DisplayName("e-mail inexistente devolve a mesma mensagem do que a senha errada")
    void emailInexistenteMesmaMensagem() throws Exception {
        criarEntrevistador("ana@exemplo.com", "segredo1");

        String corpoInexistente = mockMvc.perform(post("/login")
                        .param("email", "ninguem@exemplo.com")
                        .param("senha", "qualquer"))
                .andExpect(status().isOk())
                .andExpect(view().name("login/form"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String corpoSenhaErrada = mockMvc.perform(post("/login")
                        .param("email", "ana@exemplo.com")
                        .param("senha", "errada"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // O form ecoa o e-mail digitado no input, então o HTML completo dos
        // dois casos difere por design. O que não pode diferir é a mensagem de
        // erro: ela é a única parte que o invasor conseguiria usar para
        // descobrir se uma conta existe.
        String erroInexistente = trechoErro(corpoInexistente);
        String erroSenhaErrada = trechoErro(corpoSenhaErrada);

        assertThat(erroInexistente).isEqualTo(erroSenhaErrada)
                .isEqualTo("E-mail ou senha inválidos.");
    }

    /**
     * Extrai a mensagem de erro de credenciais do HTML renderizado.
     *
     * @param corpo HTML da página de login
     * @return texto dentro do {@code <p class="erro">}
     */
    private String trechoErro(String corpo) {
        String inicio = "<p class=\"erro\">";
        String fim = "</p>";
        int comeco = corpo.indexOf(inicio);
        int termino = corpo.indexOf(fim, comeco);
        if (comeco < 0 || termino < 0) {
            throw new AssertionError("página de login sem a mensagem de erro:\n" + corpo);
        }
        return corpo.substring(comeco + inicio.length(), termino);
    }

    @Test
    @DisplayName("a API REST continua aberta sem sessão")
    void apiFicaAberta() throws Exception {
        mockMvc.perform(get("/api/produto"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("logout invalida a sessão e volta para o login")
    void logoutInvalidaSessao() throws Exception {
        MockHttpSession sessao = SessaoAutenticacao.logada();

        mockMvc.perform(post("/logout").session(sessao))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(get("/recebedor/listar").session(sessao))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("sem entrevistador, /entrevistador/create é o bootstrap do sistema")
    void bootstrapCriaPrimeiroEntrevistador() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(content().string(containsString("Crie o primeiro entrevistador")));

        mockMvc.perform(get("/entrevistador/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("entrevistador/form"));

        mockMvc.perform(post("/entrevistador/create")
                        .param("nome", "Ana")
                        .param("email", "ana@exemplo.com")
                        .param("senha", "segredo1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/entrevistador/listar"));

        Entrevistador criado = entrevistadorRepository.findByEmail("ana@exemplo.com").orElseThrow();
        assertThat(passwordEncoder.matches("segredo1", criado.getSenhaHash())).isTrue();

        mockMvc.perform(get("/login"))
                .andExpect(content().string(not(containsString("Crie o primeiro entrevistador"))));

        mockMvc.perform(get("/entrevistador/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("o header mostra Sair quando logado e fica sem nav no bootstrap")
    void headerSeguinteSessao() throws Exception {
        mockMvc.perform(get("/recebedor/listar").session(SessaoAutenticacao.logada()))
                .andExpect(content().string(containsString(">Sair</button>")));

        mockMvc.perform(get("/entrevistador/create"))
                .andExpect(content().string(not(containsString("nav-links"))));
    }
}