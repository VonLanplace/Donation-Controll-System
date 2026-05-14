package edu.fatec.poo.model.produto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Produto {
    private Long id;
    private Long idMarcaProduto;
    private Long idTipoProduto;
    private Long idCesta;
    private String codigoBarras;

    public boolean isInCesta() {
        return idCesta != null;
    }

}
