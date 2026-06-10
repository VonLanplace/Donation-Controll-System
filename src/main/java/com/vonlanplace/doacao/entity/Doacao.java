package com.vonlanplace.doacao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "doacao")
@Data
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "nome_doador", length = 150)
    private String nomeDoador;
}