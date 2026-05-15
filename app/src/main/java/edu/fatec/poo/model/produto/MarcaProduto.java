package edu.fatec.poo.model.produto;

import edu.fatec.poo.model.IEntity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MarcaProduto implements IEntity {
    private Long id;
    private String nome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
