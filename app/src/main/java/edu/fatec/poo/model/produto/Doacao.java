package edu.fatec.poo.model.produto;

import edu.fatec.poo.model.IEntity;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Doacao implements IEntity {
    private Long id;
    private Long idMarcaProduto;
    private Long idTipoProduto;
    private Long idCesta;
    private String codigoBarras;
    private LocalDate dataValidade;

    public boolean isInCesta() {
        return idCesta != null;
    }

}
