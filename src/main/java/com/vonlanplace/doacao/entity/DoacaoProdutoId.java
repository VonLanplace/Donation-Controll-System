package com.vonlanplace.doacao.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DoacaoProdutoId implements Serializable {
    private UUID doacao;
    private UUID produto;
}