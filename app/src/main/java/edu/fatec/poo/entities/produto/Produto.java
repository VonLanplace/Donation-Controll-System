package edu.fatec.poo.entities.produto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Produto {
    private Long id;
    private Long id_MarcaProduto;
    private Long id_TipoProduto;
    private Long id_Cesta;
    private String codigo_barras;

    public boolean isInCesta() {
        return id_Cesta != null;
    }

}
