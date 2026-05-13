package com.codigo.gourmet.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {
    private int idPedido;
    private List<ItemPedido> items;
    private double total;
    private LocalDateTime fechaRegistro;

    public Pedido(int idPedido) {
        this.idPedido = idPedido;
        this.items = new ArrayList<>();
        this.total = 0;
    }

    public void agregarItem(ItemPedido item) {
        this.items.add(item);
        this.total += item.getSubtotal();
    }

    public double getTotal() {
        return total;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public List<ItemPedido> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void mostrarResumen() {
        System.out.println("Resumen Pedido #" + idPedido);
        items.forEach(item -> System.out.println("- " + item.toString()));
        System.out.println("TOTAL A PAGAR: $" + total);
    }
}
