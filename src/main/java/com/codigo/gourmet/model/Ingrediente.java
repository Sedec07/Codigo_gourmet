package com.codigo.gourmet.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "ingredientes")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    private double cantidadDisponible;

    @Enumerated(EnumType.STRING)
    private UnidadMedida unidad;

    public Ingrediente() {}

    public Ingrediente(String nombre, double cantidadDisponible, UnidadMedida unidad) {
        this.nombre = nombre;
        this.cantidadDisponible = cantidadDisponible;
        this.unidad = unidad;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(double cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
    public UnidadMedida getUnidad() { return unidad; }
    public void setUnidad(UnidadMedida unidad) { this.unidad = unidad; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingrediente that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Ingrediente{id=" + id + ", nombre='" + nombre + "'}";
    }
}
