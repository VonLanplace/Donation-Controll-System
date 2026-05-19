package edu.fatec.poo.model;

import edu.fatec.poo.util.Acesso;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Usuario implements IEntity {
    private Long Id;
    private Acesso acesso;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private long telefone;

    @Override
    public Long getId() {
        return Id;
    }

    @Override
    public void setId(Long id) {
        Id = id;
    }

    public String resetarSenha() {
        this.setSenha("1234");
        return getSenha();
    }
}
