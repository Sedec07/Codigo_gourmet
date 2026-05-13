package com.codigo.gourmet.service;

import com.codigo.gourmet.model.ItemPedido;
import com.codigo.gourmet.model.Pedido;
import com.codigo.gourmet.model.Producto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TiendaMemoria {

    private final List<Producto> catalogo = new ArrayList<>();
    private final List<Pedido> pedidosGuardados = new ArrayList<>();
    private final AtomicInteger idsPedido = new AtomicInteger(1);

    @PostConstruct
    public void init() {
        catalogo.add(new Producto("Hamburguesa doble", 18000, 10));
        catalogo.add(new Producto("Malteada vainilla", 7500, 15));
        catalogo.add(new Producto("Papas medianas", 5000, 20));
    }

    public List<Producto> getCatalogo() {
        return Collections.unmodifiableList(catalogo);
    }

    public Producto productoPorIndice(int indice) {
        return catalogo.get(indice);
    }

    public int tamanoCatalogo() {
        return catalogo.size();
    }

    public synchronized int siguienteIdPedido() {
        return idsPedido.getAndIncrement();
    }

    public int cantidadDelProductoEnPedido(Pedido pedido, Producto producto) {
        if (pedido == null) {
            return 0;
        }
        int suma = 0;
        for (ItemPedido linea : pedido.getItems()) {
            if (linea.getProducto() == producto) {
                suma += linea.getCantidad();
            }
        }
        return suma;
    }

    public int disponibleConsiderandoBorrador(Pedido borrador, Producto producto) {
        return producto.getStock() - cantidadDelProductoEnPedido(borrador, producto);
    }

    public synchronized boolean guardarPedido(Pedido pedido) {
        if (!hayStockSuficienteParaPedido(pedido)) {
            return false;
        }
        descontarStockPorPedido(pedido);
        pedido.setFechaRegistro(LocalDateTime.now());
        pedidosGuardados.add(pedido);
        return true;
    }

    public List<Pedido> listarPedidosMasRecientesPrimero() {
        List<Pedido> copia = new ArrayList<>(pedidosGuardados);
        Collections.reverse(copia);
        return Collections.unmodifiableList(copia);
    }

    public double totalVendidoHoy() {
        LocalDate hoy = LocalDate.now();
        double suma = 0;
        for (Pedido p : pedidosGuardados) {
            if (p.getFechaRegistro() != null && p.getFechaRegistro().toLocalDate().equals(hoy)) {
                suma += p.getTotal();
            }
        }
        return suma;
    }

    private boolean hayStockSuficienteParaPedido(Pedido pedido) {
        for (Map.Entry<Producto, Integer> e : cantidadesAgregadasPorProducto(pedido).entrySet()) {
            if (e.getKey().getStock() < e.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void descontarStockPorPedido(Pedido pedido) {
        for (Map.Entry<Producto, Integer> e : cantidadesAgregadasPorProducto(pedido).entrySet()) {
            Producto p = e.getKey();
            p.setStock(p.getStock() - e.getValue());
        }
    }

    private Map<Producto, Integer> cantidadesAgregadasPorProducto(Pedido pedido) {
        Map<Producto, Integer> mapa = new HashMap<>();
        for (ItemPedido linea : pedido.getItems()) {
            mapa.merge(linea.getProducto(), linea.getCantidad(), Integer::sum);
        }
        return mapa;
    }

    public Pedido buscarPedidoPorId(int idPedido) {
        for (Pedido p : pedidosGuardados) {
            if (p.getIdPedido() == idPedido) {
                return p;
            }
        }
        return null;
    }
}
