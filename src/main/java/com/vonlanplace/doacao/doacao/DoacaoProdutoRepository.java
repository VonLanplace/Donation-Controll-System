package com.vonlanplace.doacao.doacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA de {@link DoacaoProduto}, com chave composta
 * {@link DoacaoProdutoId}.
 *
 * <p>Não declara consultas próprias. Os itens são sempre lidos e gravados junto com a
 * doação que os contém, pelo cascade de {@code Doacao.produtos}, então o serviço não usa
 * este repositório para a troca de itens — ele precisa de um {@code flush} entre o
 * {@code deleteAll} e o {@code saveAll} justamente porque este repositório existe por
 * baixo. Ele fica disponível para consultas diretas e para os testes.</p>
 */
@Repository
public interface DoacaoProdutoRepository extends JpaRepository<DoacaoProduto, DoacaoProdutoId> {
}
