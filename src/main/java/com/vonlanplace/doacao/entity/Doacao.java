package com.vonlanplace.doacao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class Doacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // <--- Deixa o Hibernate/Provedor gerar automaticamente
    @Column(columnDefinition = "uuid")
    private UUID id;
    private LocalDate data;
    private String nomeDoador;
}
