package com.vonlanplace.doacao.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Impede que um controller de página novo fique sem tratamento de erro.
 *
 * <p><strong>Este teste existe porque a lista do advice é uma obrigação
 * manual, e obrigação manual não é código.</strong> O
 * {@link ViewExceptionHandler} restringe o escopo com
 * {@code assignableTypes}, e cada {@code *ViewController} criado depois precisa
 * entrar na lista. Esquecer não dá erro de compilação: o advice simplesmente
 * deixa de alcançar aquele controller, e a falha aparece em produção, como uma
 * página que devolve {@code problem+json} onde o usuário esperava HTML, ou um
 * 500 em vez da página de 404.</p>
 *
 * <p>Reler o advice não prova nada — a omissão é justamente o que a leitura não
 * mostra. Comparar a lista com o que existe no contexto, sim.</p>
 */
@SpringBootTest
@DisplayName("Escopo do ViewExceptionHandler cobre todo controller de página")
class ViewExceptionHandlerEscopoTest {

    @Autowired
    private ApplicationContext contexto;

    @Test
    @DisplayName("todo *ViewController e a Home estão em assignableTypes")
    void todoControllerDePaginaEstaNoAdvice() {
        List<Class<?>> declarados = List.of(
                ViewExceptionHandler.class.getAnnotation(ControllerAdvice.class).assignableTypes());
        Set<String> nomesDeclarados = declarados.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());
        Set<String> beansDePagina = new HashSet<>(controllersDePagina());

        assertThat(nomesDeclarados)
                .as("assignableTypes cita classe que não é mais controller de página: %s",
                        declarados.stream().map(Class::getSimpleName).toList())
                .allSatisfy(nome -> assertThat(beansDePagina)
                        .as("classe citada no advice que não existe mais no contexto: %s", nome)
                        .contains(nome));

        assertThat(beansDePagina)
                .as("controller de página fora do assignableTypes do ViewExceptionHandler: ele "
                        + "responderia JSON em 404 em vez da página HTML. "
                        + "Adicione a classe em ViewExceptionHandler.")
                .allSatisfy(nome -> assertThat(nomesDeclarados).contains(nome));
    }

    @Test
    @DisplayName("o advice de view tem precedência sobre o de API")
    void precedenciaDoAdviceDeView() {
        assertThat(ViewExceptionHandler.class.getAnnotation(org.springframework.core.annotation.Order.class))
                .as("sem @Order(HIGHEST_PRECEDENCE) o advice da API vence o de view, "
                        + "porque @RestControllerAdvice também alcança @Controller")
                .isNotNull();
        assertThat(ViewExceptionHandler.class.getAnnotation(org.springframework.core.annotation.Order.class).value())
                .isEqualTo(org.springframework.core.Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * Lista os nomes simples dos beans que são controller de página.
     *
     * <p>O critério é o mesmo que a documentação do projeto: {@code @Controller}
     * com nome terminado em {@code ViewController}, mais a {@code HomeController},
     * que é a única página que não tem CRUD e por isso não segue o sufixo. Um
     * {@code @RestController} também é {@code @Controller} por metanotação, então
     * o filtro por nome é o que separa as duas superfícies.</p>
     *
     * @return nomes simples dos controllers de página
     */
    private List<String> controllersDePagina() {
        return Stream.of(contexto.getBeanNamesForAnnotation(Controller.class))
                .map(contexto::getBean)
                .map(Object::getClass)
                .map(Class::getSimpleName)
                .filter(nome -> nome.endsWith("ViewController") || nome.equals("HomeController"))
                .sorted()
                .toList();
    }
}
