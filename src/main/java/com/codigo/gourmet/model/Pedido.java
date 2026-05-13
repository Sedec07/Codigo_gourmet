package com.codigo.gourmet.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    private String nombreCliente;
    private String estado;
    private LocalDateTime fechaRegistro;
    private double total;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items = new ArrayList<>();

    public Pedido() {}

    public Pedido(String nombreCliente) {
        this.nombreCliente = nombreCliente;
        this.estado = "PENDIENTE";
        this.fechaRegistro = LocalDateTime.now();
    }

    public void agregarItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
        total += item.getSubtotal();
    }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public List<ItemPedido> getItems() { return Collections.unmodifiableList(items); }
    public void setItems(List<ItemPedido> items) { this.items = items; }

    public void mostrarResumen() {
        System.out.println("Resumen Pedido #" + idPedido + " (" + nombreCliente + ") [" + estado + "]");
        items.forEach(item -> System.out.println("- " + item.toString()));
        System.out.println("TOTAL A PAGAR: $" + total);
    }
}
