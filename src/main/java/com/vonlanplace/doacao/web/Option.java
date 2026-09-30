package com.vonlanplace.doacao.web;

/**
 * Par valor/rótulo para popular um {@code <select>}.
 *
 * <p><strong>Existe porque o valor gravado e o texto exibido quase nunca são o
 * mesmo.</strong> Marca e tipo são gravados pelo nome e exibidos pelo nome, mas
 * {@code OrgaoEmissorRG} grava {@code SSP} e exibe
 * {@code "Secretaria de Segurança Pública"}, e o id de um recebedor é um UUID
 * que ninguém quer ver. Um {@code <option>} precisa das duas coisas separadas,
 * e um record dá acesso por getter, que é o que o Thymeleaf exige.</p>
 *
 * @param valor  valor enviado no formulário
 * @param rotulo texto exibido na lista
 */
public record Option(String valor, String rotulo) {
}
