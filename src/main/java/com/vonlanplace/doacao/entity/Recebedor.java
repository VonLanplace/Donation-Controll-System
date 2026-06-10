package com.vonlanplace.doacao.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "recebedor", uniqueConstraints = {
        @UniqueConstraint(name = "uk_recebedor_cpf", columnNames = "cpf"),
        @UniqueConstraint(name = "uk_recebedor_email", columnNames = "email")
})
@Data
public class Recebedor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Column(name = "cpf", length = 11)
    private String cpf;

    @Column(name = "rg_num", length = 20)
    private String rgNum;

    @Enumerated(EnumType.STRING)
    @Column(name = "rg_emissor", length = 20)
    private OrgaoEmissorRG rgEmissor;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "telefone_pessoal", length = 15)
    private String telefonePessoal;

    @Column(name = "telefone_recado", length = 15)
    private String telefoneRecado;

    @Column(name = "le_escreve", nullable = false)
    private Boolean leEscreve = false;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "pix", length = 100)
    private String pix;
}