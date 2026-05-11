package edu.fatec.poo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cesta {
    private Long id;
    private LocalDate dataEntrega;

    public boolean isEntreque() {
        return dataEntrega != null;
    }
}
