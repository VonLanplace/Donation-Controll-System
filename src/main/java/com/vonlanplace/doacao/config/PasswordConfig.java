package com.vonlanplace.doacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração do codificador de senha.
 *
 * <p><strong>O codificador tem dois consumidores.</strong>
 * {@code EntrevistadorService} usa {@code encode(...)} no cadastro e na
 * mudança de senha, e {@code AutenticacaoService} usa {@code matches(...)} no
 * login. Ambas as funções dependem deste bean; nenhum dos dois serviços cria
 * o seu próprio encoders.</p>
 */
@Configuration
public class PasswordConfig {

    /**
     * Expõe o codificador BCrypt como bean do contexto.
     *
     * @return instância de {@link BCryptPasswordEncoder}, com o custo padrão
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
