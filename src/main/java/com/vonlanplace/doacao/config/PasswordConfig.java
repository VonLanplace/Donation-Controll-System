package com.vonlanplace.doacao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração do codificador de senha.
 *
 * <p><strong>Este bean está em uso, mas não tem consumidor.</strong>
 * {@code Entrevistador} guarda a senha em {@code senha_hash}, e o {@code PasswordEncoder}
 * é o que deveria produzir esse valor, mas não existe {@code EntrevistadorService} nem
 * qualquer chamada a {@code encode(...)} no projeto. O bean é instanciado pelo
 * contexto e injetado em lugar nenhum.</p>
 *
 * <p>Se for necessário gravar senha de entrevistador, este é o ponto certo: injetar o
 * encoder no serviço e chamar {@code encode(senha)} antes de persistir. Guardar a senha
 * em texto puro e depois aplicar BCrypt na migração também funciona, mas deixa
 * credencial legível no banco durante o caminho.</p>
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
