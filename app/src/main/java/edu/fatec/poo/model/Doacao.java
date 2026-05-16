package edu.fatec.poo.model;

import edu.fatec.poo.model.produto.Produto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Doacao implements IEntity {
    private Long id;
    private String nomeDoador;
    private LocalDate data;
    private Usuario cadastrante;
    private List<Produto> produtos;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getDataValidadeDdMmYyyy() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        return data.format(dtf);
    }
}
