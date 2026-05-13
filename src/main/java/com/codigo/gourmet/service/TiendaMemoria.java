package com.codigo.gourmet.service;

import com.codigo.gourmet.model.Ingrediente;
import com.codigo.gourmet.model.ItemPedido;
import com.codigo.gourmet.model.LineaReceta;
import com.codigo.gourmet.model.Pedido;
import com.codigo.gourmet.model.Producto;
import com.codigo.gourmet.model.UnidadMedida;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TiendaMemoria {

    private static final double UMBRAL = 1e-6;

    private final List<Producto> catalogo = new ArrayList<>();
    private final List<Pedido> pedidosGuardados = new ArrayList<>();
    private final AtomicInteger idsPedido = new AtomicInteger(1);

    private final List<Ingrediente> ingredientes = new CopyOnWriteArrayList<>();
    private final Map<Producto, List<LineaReceta>> recetasPorProducto = new HashMap<>();
    private final AtomicInteger idsIngrediente = new AtomicInteger(1);

    public TiendaMemoria() {
        catalogo.add(new Producto("Hamburguesa doble", 18000, 10));
        catalogo.add(new Producto("Malteada vainilla", 7500, 15));
        catalogo.add(new Producto("Papas medianas", 5000, 20));

        ingredientes.add(new Ingrediente(idsIngrediente.getAndIncrement(), "Carne molida", 5000, UnidadMedida.GRAMOS));
        ingredientes.add(new Ingrediente(idsIngrediente.getAndIncrement(), "Pan de hamburguesa", 80, UnidadMedida.UNIDADES));
        ingredientes.add(new Ingrediente(idsIngrediente.getAndIncrement(), "Leche", 10000, UnidadMedida.ML));
        ingredientes.add(new Ingrediente(idsIngrediente.getAndIncrement(), "Papa cruda", 30000, UnidadMedida.GRAMOS));

        System.out.println("[TiendaMemoria] instancia=" + System.identityHashCode(this)
                + " catalogo=" + catalogo.size() + " ingredientes=" + ingredientes.size());
    }

    /**
     * Garantiza la misma referencia que el catálogo en memoria (clave de recetas y stock).
     * Si el pedido en sesión conserva otra instancia con el mismo nombre, el HashMap de recetas no coincidía.
     */
    private Producto canonicalProducto(Producto ref) {
        if (ref == null) {
            return null;
        }
        for (Producto p : catalogo) {
            if (p == ref) {
                return p;
            }
        }
        String nombre = ref.getNombre();
        if (nombre != null) {
            for (Producto p : catalogo) {
                if (nombre.equals(p.getNombre())) {
                    return p;
                }
            }
        }
        return ref;
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
        Producto canon = canonicalProducto(producto);
        int suma = 0;
        for (ItemPedido linea : pedido.getItems()) {
            if (canonicalProducto(linea.getProducto()) == canon) {
                suma += linea.getCantidad();
            }
        }
        return suma;
    }

    public int disponibleConsiderandoBorrador(Pedido borrador, Producto producto) {
        Producto canon = canonicalProducto(producto);
        return canon.getStock() - cantidadDelProductoEnPedido(borrador, canon);
    }

    public List<Ingrediente> getIngredientes() {
        System.out.println("[TiendaMemoria] getIngredientes inst=" + System.identityHashCode(this)
                + " size=" + ingredientes.size());
        return List.copyOf(ingredientes);
    }

    public Ingrediente buscarIngredientePorId(int id) {
        for (Ingrediente i : ingredientes) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    public synchronized Ingrediente registrarIngrediente(String nombre, double cantidadInicial, UnidadMedida unidad) {
        int antes = ingredientes.size();
        int id = idsIngrediente.getAndIncrement();
        Ingrediente ing = new Ingrediente(id, nombre.trim(), cantidadInicial, unidad);
        ingredientes.add(ing);
        System.out.println("[TiendaMemoria] registrarIngrediente inst=" + System.identityHashCode(this)
                + " antes=" + antes + " despues=" + ingredientes.size()
                + " id=" + id + " nombre=" + ing.getNombre());
        return ing;
    }

    public List<LineaReceta> lineasRecetaDeProducto(Producto producto) {
        Producto canon = canonicalProducto(producto);
        synchronized (this) {
            List<LineaReceta> lineas = recetasPorProducto.getOrDefault(canon, Collections.emptyList());
            System.out.println("[TiendaMemoria] lineasRecetaDeProducto inst=" + System.identityHashCode(this)
                    + " prodCanon=" + System.identityHashCode(canon) + " nombre=" + canon.getNombre()
                    + " lineas=" + lineas.size() + " clavesMapa=" + recetasPorProducto.size());
            return List.copyOf(lineas);
        }
    }

    public synchronized void agregarLineaReceta(int indiceProducto, int idIngrediente, double cantidadPorUnidadProducto) {
        if (indiceProducto < 0 || indiceProducto >= catalogo.size()) {
            return;
        }
        if (cantidadPorUnidadProducto <= 0) {
            return;
        }
        Ingrediente ing = buscarIngredientePorId(idIngrediente);
        if (ing == null) {
            return;
        }
        Producto producto = catalogo.get(indiceProducto);
        List<LineaReceta> lineas = recetasPorProducto.computeIfAbsent(producto, p -> new ArrayList<>());
        for (LineaReceta lr : lineas) {
            if (lr.getIngrediente().getId() == idIngrediente) {
                lr.setCantidadPorUnidadProducto(lr.getCantidadPorUnidadProducto() + cantidadPorUnidadProducto);
                System.out.println("[TiendaMemoria] agregarLineaReceta MERGE inst=" + System.identityHashCode(this)
                        + " producto=" + producto.getNombre() + " lineas=" + lineas.size());
                return;
            }
        }
        lineas.add(new LineaReceta(ing, cantidadPorUnidadProducto));
        System.out.println("[TiendaMemoria] agregarLineaReceta ADD inst=" + System.identityHashCode(this)
                + " producto=" + producto.getNombre() + " ingId=" + idIngrediente
                + " cantPorU=" + cantidadPorUnidadProducto + " lineas=" + lineas.size());
    }

    public synchronized void quitarLineaRecetaIngrediente(int indiceProducto, int idIngrediente) {
        if (indiceProducto < 0 || indiceProducto >= catalogo.size()) {
            return;
        }
        Producto producto = catalogo.get(indiceProducto);
        List<LineaReceta> lineas = recetasPorProducto.get(producto);
        if (lineas == null) {
            return;
        }
        lineas.removeIf(lr -> lr.getIngrediente().getId() == idIngrediente);
        if (lineas.isEmpty()) {
            recetasPorProducto.remove(producto);
        }
    }

    public synchronized GuardarPedidoResultado guardarPedido(Pedido pedido) {
        System.out.println("[TiendaMemoria] guardarPedido IN inst=" + System.identityHashCode(this)
                + " idPedido=" + pedido.getIdPedido() + " items=" + pedido.getItems().size()
                + " pedidosGuardadosAntes=" + pedidosGuardados.size());
        if (!hayStockSuficienteParaPedido(pedido)) {
            System.out.println("[TiendaMemoria] guardarPedido -> STOCK_PRODUCTO_INSUFICIENTE");
            return GuardarPedidoResultado.STOCK_PRODUCTO_INSUFICIENTE;
        }
        if (!hayIngredientesSuficientesParaPedido(pedido)) {
            System.out.println("[TiendaMemoria] guardarPedido -> INGREDIENTES_INSUFICIENTES");
            return GuardarPedidoResultado.INGREDIENTES_INSUFICIENTES;
        }
        descontarStockPorPedido(pedido);
        descontarIngredientesPorPedido(pedido);
        pedido.setFechaRegistro(LocalDateTime.now());
        pedidosGuardados.add(pedido);
        System.out.println("[TiendaMemoria] guardarPedido OK pedidosGuardadosDespues=" + pedidosGuardados.size());
        return GuardarPedidoResultado.OK;
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
            Producto clave = canonicalProducto(linea.getProducto());
            mapa.merge(clave, linea.getCantidad(), Integer::sum);
        }
        return mapa;
    }

    private Map<Ingrediente, Double> consumoIngredientesPorPedido(Pedido pedido) {
        Map<Ingrediente, Double> mapa = new HashMap<>();
        for (ItemPedido item : pedido.getItems()) {
            Producto clave = canonicalProducto(item.getProducto());
            List<LineaReceta> copiaLineas;
            synchronized (this) {
                List<LineaReceta> lineas = recetasPorProducto.getOrDefault(clave, Collections.emptyList());
                copiaLineas = List.copyOf(lineas);
            }
            for (LineaReceta lr : copiaLineas) {
                double consumo = lr.getCantidadPorUnidadProducto() * item.getCantidad();
                mapa.merge(lr.getIngrediente(), consumo, Double::sum);
            }
        }
        return mapa;
    }

    private boolean hayIngredientesSuficientesParaPedido(Pedido pedido) {
        for (Map.Entry<Ingrediente, Double> e : consumoIngredientesPorPedido(pedido).entrySet()) {
            if (e.getKey().getCantidadDisponible() + UMBRAL < e.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void descontarIngredientesPorPedido(Pedido pedido) {
        for (Map.Entry<Ingrediente, Double> e : consumoIngredientesPorPedido(pedido).entrySet()) {
            Ingrediente ing = e.getKey();
            ing.setCantidadDisponible(ing.getCantidadDisponible() - e.getValue());
        }
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
