package com.vonlanplace.doacao.recebedor.form;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Valida as linhas dinâmicas do formulário unificado.
 *
 * <p>As linhas de dependentes, auxílios e despesas são adicionadas pelo usuário
 * e podem chegar completamente vazias. A validação de bean não sabe distinguir
 * uma linha vazia de uma linha preenchida pela metade, então este validator
 * ignora as vazias e exige os campos obrigatórios apenas das preenchidas.</p>
 *
 * <p>Os campos pessoais e residenciais continuam validados pelas anotações de
 * bean validation no próprio {@link RecebedorFormDTO} e em
 * {@link ResidenciaFormDTO}.</p>
 */
@Component
public class RecebedorFormValidator implements Validator {

    private static final BigDecimal VALOR_MINIMO = new BigDecimal("0.01");

    @Override
    public boolean supports(Class<?> clazz) {
        return RecebedorFormDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RecebedorFormDTO form = (RecebedorFormDTO) target;
        validarDependentes(form.getDependentes(), errors);
        validarAuxilios(form.getAuxilios(), errors);
        validarDespesas(form.getDespesas(), errors);
    }

    private void validarDependentes(List<DependenteFormDTO> linhas, Errors errors) {
        if (linhas == null) {
            return;
        }
        for (int i = 0; i < linhas.size(); i++) {
            DependenteFormDTO linha = linhas.get(i);
            if (linha == null || !linhaPreenchida(linha)) {
                continue;
            }
            String prefixo = "dependentes[" + i + "].";
            if (textoVazio(linha.getNome())) {
                errors.rejectValue(prefixo + "nome", "dependente.nome.obrigatorio",
                        "O nome do dependente é obrigatório");
            } else if (linha.getNome().length() > 150) {
                errors.rejectValue(prefixo + "nome", "dependente.nome.tamanho",
                        "O nome do dependente deve ter no máximo 150 caracteres");
            }
            if (linha.getIdade() < 0) {
                errors.rejectValue(prefixo + "idade", "dependente.idade.negativa",
                        "A idade não pode ser negativa");
            }
            if (linha.getDataNascimento() != null
                    && !linha.getDataNascimento().isBefore(LocalDate.now())) {
                errors.rejectValue(prefixo + "dataNascimento", "dependente.dataNascimento.passado",
                        "A data de nascimento deve ser no passado");
            }
        }
    }

    private void validarAuxilios(List<AuxilioFormDTO> linhas, Errors errors) {
        if (linhas == null) {
            return;
        }
        for (int i = 0; i < linhas.size(); i++) {
            AuxilioFormDTO linha = linhas.get(i);
            if (linha == null || !linhaPreenchida(linha)) {
                continue;
            }
            validarValor(linha.getValor(), "auxilios[" + i + "].valor", errors);
            validarFonte(linha.getFonte(), "auxilios[" + i + "].fonte", errors);
            validarDescricao(linha.getDescricao(), "auxilios[" + i + "].descricao", errors);
        }
    }

    private void validarDespesas(List<DespesaFormDTO> linhas, Errors errors) {
        if (linhas == null) {
            return;
        }
        for (int i = 0; i < linhas.size(); i++) {
            DespesaFormDTO linha = linhas.get(i);
            if (linha == null || !linhaPreenchida(linha)) {
                continue;
            }
            validarValor(linha.getValor(), "despesas[" + i + "].valor", errors);
            validarFonte(linha.getFonte(), "despesas[" + i + "].fonte", errors);
            validarDescricao(linha.getDescricao(), "despesas[" + i + "].descricao", errors);
        }
    }

    private void validarValor(BigDecimal valor, String campo, Errors errors) {
        if (valor == null) {
            errors.rejectValue(campo, "valor.obrigatorio", "O valor é obrigatório");
        } else if (valor.compareTo(VALOR_MINIMO) < 0) {
            errors.rejectValue(campo, "valor.positivo", "O valor deve ser positivo");
        }
    }

    private void validarFonte(String fonte, String campo, Errors errors) {
        if (textoVazio(fonte)) {
            errors.rejectValue(campo, "fonte.obrigatoria", "A fonte é obrigatória");
        } else if (fonte.length() > 100) {
            errors.rejectValue(campo, "fonte.tamanho", "A fonte deve ter no máximo 100 caracteres");
        }
    }

    private void validarDescricao(String descricao, String campo, Errors errors) {
        if (descricao != null && descricao.length() > 500) {
            errors.rejectValue(campo, "descricao.tamanho",
                    "A descrição deve ter no máximo 500 caracteres");
        }
    }

    private boolean linhaPreenchida(DependenteFormDTO linha) {
        return linha.getId() != null
                || !textoVazio(linha.getNome())
                || linha.getIdade() != 0
                || linha.getDataNascimento() != null;
    }

    private boolean linhaPreenchida(AuxilioFormDTO linha) {
        return linha.getId() != null
                || linha.getValor() != null
                || !textoVazio(linha.getFonte())
                || !textoVazio(linha.getDescricao());
    }

    private boolean linhaPreenchida(DespesaFormDTO linha) {
        return linha.getId() != null
                || linha.getValor() != null
                || !textoVazio(linha.getFonte())
                || !textoVazio(linha.getDescricao());
    }

    private boolean textoVazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
