package com.codigo.gourmet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "lineas_receta")
public class LineaReceta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonIgnore
    private Producto producto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ingrediente_id")
    private Ingrediente ingrediente;

    private double cantidadPorUnidadProducto;

    public LineaReceta() {}

    public LineaReceta(Ingrediente ingrediente, double cantidadPorUnidadProducto) {
        this.ingrediente = ingrediente;
        this.cantidadPorUnidadProducto = cantidadPorUnidadProducto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Ingrediente getIngrediente() { return ingrediente; }
    public void setIngrediente(Ingrediente ingrediente) { this.ingrediente = ingrediente; }
    public double getCantidadPorUnidadProducto() { return cantidadPorUnidadProducto; }
    public void setCantidadPorUnidadProducto(double cantidadPorUnidadProducto) { this.cantidadPorUnidadProducto = cantidadPorUnidadProducto; }

    public double getCosto() {
        return ingrediente != null ? ingrediente.getCosto() * cantidadPorUnidadProducto : 0;
    }
}
