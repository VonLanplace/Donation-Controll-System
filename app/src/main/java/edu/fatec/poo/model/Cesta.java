package edu.fatec.poo.model;

import java.time.LocalDate;
import java.util.Objects;

public class Cesta {
    private Long id;
    private LocalDate dataEntrega;

    public Cesta() {
    }

    public Cesta(Long id, LocalDate dataEntrega) {
        this.id = id;
        this.dataEntrega = dataEntrega;
    }

    public boolean isEntreque() {
        return dataEntrega != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cesta cesta = (Cesta) o;
        return Objects.equals(id, cesta.id) && Objects.equals(dataEntrega, cesta.dataEntrega);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataEntrega);
    }

    @Override
    public String
    toString() {
        return "Cesta{" +
                "id=" + id +
                ", dataEntrega=" + dataEntrega +
                '}';
    }
}
