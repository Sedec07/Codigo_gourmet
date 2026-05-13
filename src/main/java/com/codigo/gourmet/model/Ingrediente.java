package com.codigo.gourmet.model;

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

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(double cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public UnidadMedida getUnidad() {
        return unidad;
    }
}
