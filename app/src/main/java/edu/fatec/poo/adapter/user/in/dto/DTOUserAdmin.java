package edu.fatec.poo.adapter.user.in.dto;

import edu.fatec.poo.adapter.user.in.UserIn;
import edu.fatec.poo.exceptions.invalido.*;
import edu.fatec.poo.util.Acesso;

public record DTOUserAdmin(
        String nome,
        String cpf,
        String telefone,
        String email,
        Acesso acesso,
        boolean resetarSenha
) implements UserIn {
    public DTOUserAdmin {
        // 1. Nome
        if (nome == null || nome.isBlank())
            throw new NomeInvalidoException("Nome de usuário inválido.");
        nome = nome.trim();

        // 2. CPF
        if (cpf == null)
            throw new CpfInvalidoException("CPF não pode ser nulo.");

        cpf = cpf.trim().replaceAll("[^0-9]", "");
        if (cpf.length() != 11)
            throw new CpfInvalidoException("CPF deve conter 11 dígitos numéricos.");
        cpf = cpf.trim();

        // 3. Telefone
        if (telefone == null) {
            throw new TelefoneInvalidoException("Telefone não pode ser nulo.");
        }
        String telLimpo = telefone.replaceAll("[^0-9]", "");
        if (telLimpo.length() < 10 || telLimpo.length() > 11) {
            throw new TelefoneInvalidoException("Número de Telefone deve ter 10 ou 11 dígitos.");
        }
        telefone = telLimpo;

        // 4. Email
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
            throw new EmailInvalidoException("Formato de Email inválido.");
        email = email.trim();

        // 5. Acesso
        if (acesso == null)
            throw new AcessoInvalidoException("Acesso inválido ou não informado.");

    }

    @Override
    public String getCpf() {
        return cpf;
    }


    @Override
    public String getEmail() {
        return email;
    }
    
}
