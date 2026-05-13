package edu.fatec.poo.model;

import edu.fatec.poo.util.Acesso;
import lombok.Data;

@Data
public class Usuario implements IEntity {
    private long Id;
    private Acesso acesso;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private int telefone;

    @Override
    public long getId() {
        return Id;
    }

    @Override
    public void setId(long id) {
        Id = id;
    }
}
