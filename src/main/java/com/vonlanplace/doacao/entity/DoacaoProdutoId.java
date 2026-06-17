package com.vonlanplace.doacao.entity;

import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DoacaoProdutoId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "doacao_id", nullable = false)
    private UUID doacao;

    @Column(name = "produto_id", nullable = false)
    private UUID produto;
}