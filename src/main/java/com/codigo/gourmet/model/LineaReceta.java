package com.codigo.gourmet.model;

public class LineaReceta {
    private final Ingrediente ingrediente;
    private double cantidadPorUnidadProducto;

    public LineaReceta(Ingrediente ingrediente, double cantidadPorUnidadProducto) {
        this.ingrediente = ingrediente;
        this.cantidadPorUnidadProducto = cantidadPorUnidadProducto;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public double getCantidadPorUnidadProducto() {
        return cantidadPorUnidadProducto;
    }

    public void setCantidadPorUnidadProducto(double cantidadPorUnidadProducto) {
        this.cantidadPorUnidadProducto = cantidadPorUnidadProducto;
    }
}
