package com.codigo.gourmet.model;

import java.util.Objects;

public class Ingrediente {
    private final int id;
    private final String nombre;
    private double cantidadDisponible;
    private final UnidadMedida unidad;

    public Ingrediente(int id, String nombre, double cantidadDisponible, UnidadMedida unidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidadDisponible = cantidadDisponible;
        this.unidad = unidad;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public double getCantidadDisponible() { return cantidadDisponible; }
    public UnidadMedida getUnidad() { return unidad; }

    public void setCantidadDisponible(double cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingrediente that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ingrediente{" + "id=" + id + ", nombre='" + nombre + '\'' + '}';
    }
}
