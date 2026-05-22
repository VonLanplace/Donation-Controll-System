package edu.fatec.poo.model;

import edu.fatec.poo.model.produto.Produto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Doacao {
    private UUID id = UUID.randomUUID();
    private String nomeDoador;
    private LocalDate data;
    private Usuario cadastrante;
    private List<Produto> produtos;
    
    public String getDataValidadeDdMmYyyy() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        return data.format(dtf);
    }
}
