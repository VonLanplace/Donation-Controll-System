package com.vonlanplace.doacao.config;

import com.vonlanplace.doacao.web.EntrevistadorAutenticadoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração web comum a todas as superfícies.
 *
 * <p><strong>Registra o interceptor que fecha as rotas web.</strong> O padrão
 * <code>/**</code> cobre os {@code *ViewController} presentes e os que vierem:
 * um controller de página novo fica protegido sem nada a fazer. As exclusões
 * são as superfícies que não são de página:</p>
 *
 * <ul>
 *   <li>{@code /login}, {@code /logout} — onde a sessão nasce e morre;</li>
 *   <li>{@code /api/**} — a API REST continua sem autenticação, contrato atual;</li>
 *   <li>{@code /css/**, /js/**, /images/**, /favicon.ico, /webjars/**} — recursos estáticos do navegador;</li>
 *   <li>{@code /error} — a página de erro técnica não pode depender de sessão;</li>
 *   <li>{@code /h2-console/**} e {@code /test/**} — consola de banco e scratch de desenvolvimento.</li>
 * </ul>
 *
 * <p><strong>Um conector de rota nunca pode ser pública por acidente.</strong>
 * Se amanhã uma rota de página nova estiver fora do padrão de nome e precisar
 * ser pública, ela entra nesta lista de exclusões com um comentário, em vez de
 * o interceptor ser desligado globalmente.</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final EntrevistadorAutenticadoInterceptor entrevistadorAutenticadoInterceptor;

    /**
     * Construtor com injeção de dependências.
     *
     * @param entrevistadorAutenticadoInterceptor interceptor que confere a sessão
     */
    public WebConfig(EntrevistadorAutenticadoInterceptor entrevistadorAutenticadoInterceptor) {
        this.entrevistadorAutenticadoInterceptor = entrevistadorAutenticadoInterceptor;
    }

    /**
     * Registra o interceptor de autenticação nas rotas protegidas.
     *
     * @param registry registro de interceptors do Spring MVC
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(entrevistadorAutenticadoInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/logout",
                        "/api/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/webjars/**",
                        "/error",
                        "/h2-console/**",
                        "/test",
                        "/test/**"
                );
    }
}