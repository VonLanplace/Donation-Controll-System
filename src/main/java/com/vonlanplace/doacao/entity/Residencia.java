package com.vonlanplace.doacao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "residencia")
@Data
public class Residencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "logradouro", nullable = false, length = 150)
    private String logradouro;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;

    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @Column(name = "num_moradores", nullable = false)
    private Integer numMoradores = 0;

    @Column(name = "num_trabalhadores", nullable = false)
    private Integer numTrabalhadores = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ocupacao", nullable = false, length = 30)
    private TipoOcupacao tipoOcupacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_residencia", nullable = false, length = 30)
    private TipoResidencia tipoResidencia;
}