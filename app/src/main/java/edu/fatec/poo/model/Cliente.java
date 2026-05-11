package edu.fatec.poo.model;

public class Cliente implements IEntity {
    private long Id;
    private String nome;
    private String email;
    private int telefone;
    private String enderecoLogradouro;
    private String enderecoCep;
    private int enderecoNum;
    private String complemento;

    @Override
    public long getId() {
        return Id;
    }

    @Override
    public void setId(long id) {
        Id = id;
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

    public int getTelefone() {
        return telefone;
    }

    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }

    public String getEnderecoLogradouro() {
        return enderecoLogradouro;
    }

    public void setEnderecoLogradouro(String enderecoLogradouro) {
        this.enderecoLogradouro = enderecoLogradouro;
    }

    public String getEnderecoCep() {
        return enderecoCep;
    }

    public void setEnderecoCep(String enderecoCep) {
        this.enderecoCep = enderecoCep;
    }

    public int getEnderecoNum() {
        return enderecoNum;
    }

    public void setEnderecoNum(int enderecoNum) {
        this.enderecoNum = enderecoNum;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "Id=" + Id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone=" + telefone +
                ", enderecoLogradouro='" + enderecoLogradouro + '\'' +
                ", enderecoCep='" + enderecoCep + '\'' +
                ", enderecoNum=" + enderecoNum +
                ", complemento='" + complemento + '\'' +
                '}';
    }
}
