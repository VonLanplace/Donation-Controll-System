package edu.fatec.poo.model;

import edu.fatec.poo.util.Acesso;

import java.util.Objects;

public class Usuario implements IEntity {
    private Long Id;
    private Acesso acesso;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private long telefone;

    public Usuario() {
    }

    public Usuario(Long id, Acesso acesso, String nome, String email, String senha, String cpf, long telefone) {
        Id = id;
        this.acesso = acesso;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.telefone = telefone;
    }

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

    public Acesso getAcesso() {
        return acesso;
    }

    public void setAcesso(Acesso acesso) {
        this.acesso = acesso;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public long getTelefone() {
        return telefone;
    }

    public void setTelefone(long telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "Id=" + Id +
                ", acesso=" + acesso +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", cpf='" + cpf + '\'' +
                ", telefone=" + telefone +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return telefone == usuario.telefone && Objects.equals(Id, usuario.Id) && acesso == usuario.acesso && Objects.equals(nome, usuario.nome) && Objects.equals(email, usuario.email) && Objects.equals(senha, usuario.senha) && Objects.equals(cpf, usuario.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, acesso, nome, email, senha, cpf, telefone);
    }
}
