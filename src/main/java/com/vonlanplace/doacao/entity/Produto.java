package com.vonlanplace.doacao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // <--- Deixa o Hibernate/Provedor gerar automaticamente
    @Column(columnDefinition = "uuid")
    private UUID id;
    private String codigoBarras;
    private MarcaProduto marcaProduto;
    private Cesta cesta;
    private TipoProduto tipoProduto;
}
