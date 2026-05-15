package edu.fatec.poo.model.produto;

import edu.fatec.poo.model.Cesta;
import edu.fatec.poo.model.Doacao;
import edu.fatec.poo.model.IEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Produto implements IEntity {
    private Long id;
    private Doacao doacao;
    private MarcaProduto marca;
    private TipoProduto tipo;
    private Cesta cesta;
    private String codigoBarras;
    private LocalDate dataValidade;

    public boolean isInCesta() {
        return cesta != null;
    }

    public String getDataValidadeDdMmYyyy() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        return dataValidade.format(dtf);
    }
}
