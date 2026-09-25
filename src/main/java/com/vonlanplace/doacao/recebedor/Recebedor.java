package com.vonlanplace.doacao.recebedor;

import com.vonlanplace.doacao.recebedor.residencia.Residencia;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "recebedor")
@Data
public class Recebedor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    @Column(name = "cpf", unique = true, length = 11)
    @NotNull
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

    @Column(name = "pix", unique = true, length = 100)
    private String pix;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "residencia_id", nullable = false)
    private Residencia residencia;
}