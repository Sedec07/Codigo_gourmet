package com.codigo.gourmet.model;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int idPedido;
    private List<ItemPedido> items;
    private double total;

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

    public void mostrarResumen() {
        System.out.println("Resumen Pedido #" + idPedido);
        items.forEach(item -> System.out.println("- " + item.toString()));
        System.out.println("TOTAL A PAGAR: $" + total);
    }
}
