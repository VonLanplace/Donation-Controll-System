package com.vonlanplace.doacao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Cesta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // <--- Deixa o Hibernate/Provedor gerar automaticamente
    @Column(columnDefinition = "uuid")
    private UUID id;
    private Data dataEntrega;
}
