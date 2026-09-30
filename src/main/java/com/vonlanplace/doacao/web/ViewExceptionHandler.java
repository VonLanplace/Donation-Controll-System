package com.vonlanplace.doacao.web;

import com.vonlanplace.doacao.produto.controller.ProdutoViewController;
import com.vonlanplace.doacao.produto.marca.controller.MarcaProdutoViewController;
import com.vonlanplace.doacao.produto.tipo.controller.TipoProdutoViewController;
import com.vonlanplace.doacao.recebedor.controller.RecebedorViewController;
import com.vonlanplace.doacao.entrevistador.controller.EntrevistadorViewController;
import com.vonlanplace.doacao.recebedor.residencia.controller.ResidenciaViewController;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Trata exceção das telas web, antes que elas virem erro 500.
 *
 * <p><strong>Existe separado do {@code GlobalExceptionHandler} de propósito.</strong>
 * Aquele responde em {@code application/problem+json} e é voltado ao cliente da API
 * REST. Uma exceção de "não encontrado" numa página Thymeleaf não deve virar JSON:
 * quem está no browser precisa de uma página de erro com um link de volta, não de
 * um payload. Um único advice para as duas superfícies obrigaria o mesmo método a
 * decidir, em tempo de execução, se responde JSON ou HTML.</p>
 *
 * <p><strong>Restringido por {@code assignableTypes}, e não por pacote.</strong> Um
 * {@code @ControllerAdvice} sem restrição também alcança {@code @RestController},
 * porque {@code @RestController} é meta-anotado com {@code @Controller}. Se este
 * advice fosse amplo, a página de erro HTML passaria a responder às chamadas de
 * {@code /api/*} e a API perderia o {@code problem+json} do 404. Cada controller de
 * view novo precisa entrar na lista.</p>
 *
 * <p><strong>A precedência é obrigatória, não cosmética.</strong>
 * {@code GlobalExceptionHandler} é anotado com {@code @RestControllerAdvice}, e isso
 * <em>não</em> limita nada a controllers REST: {@code @RestControllerAdvice} é
 * meta-anotado com {@code @ControllerAdvice}, logo ele também alcança um
 * {@code @Controller} de view. Os dois tratam {@code EntityNotFoundException}, e sem
 * ordem definida o do REST vencia — a página de produto respondia 404 com o corpo
 * {@code problem+json} em vez do HTML. Com esta precedência, o advice de view é
 * consultado primeiro; como ele só se declara aplicável a view controllers, toda
 * chamada de {@code /api/*} cai por exclusividade no do REST e segue em JSON.</p>
 */
@ControllerAdvice(assignableTypes = {
        HomeController.class,
        ProdutoViewController.class,
        MarcaProdutoViewController.class,
        TipoProdutoViewController.class,
        ResidenciaViewController.class,
        EntrevistadorViewController.class,
        RecebedorViewController.class
})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ViewExceptionHandler {

    /**
     * Devolve 404 com página própria quando o registro não existe.
     *
     * <p>Sem isto a página de detalhe de produto responderia 500: o
     * {@code EntityNotFoundException} é levantado pela lógica de negócio, não é
     * declarado no {@code throws} de nenhum controller, e nada o traduz.</p>
     *
     * @param exception exceção original, cujo {@code getMessage()} traz o identificador
     * @param model     modelo da view
     * @return nome da view de erro
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNaoEncontrado(EntityNotFoundException exception, Model model) {
        model.addAttribute("titulo", "Registro não encontrado");
        model.addAttribute("detalhe", exception.getMessage());
        return "erro/404";
    }
}
