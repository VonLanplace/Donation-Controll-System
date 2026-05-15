package edu.fatec.poo.model.produto;

import edu.fatec.poo.model.IEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TipoProduto implements IEntity {
    private Long id;
    private String nome;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
