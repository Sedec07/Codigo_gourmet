package com.codigo.gourmet.controller;

import com.codigo.gourmet.model.Ingrediente;
import com.codigo.gourmet.model.ItemPedido;
import com.codigo.gourmet.model.LineaReceta;
import com.codigo.gourmet.model.Pedido;
import com.codigo.gourmet.model.Producto;
import com.codigo.gourmet.repository.IngredienteRepository;
import com.codigo.gourmet.repository.LineaRecetaRepository;
import com.codigo.gourmet.repository.PedidoRepository;
import com.codigo.gourmet.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final LineaRecetaRepository lineaRecetaRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ProductoRepository productoRepository,
                            IngredienteRepository ingredienteRepository,
                            LineaRecetaRepository lineaRecetaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.lineaRecetaRepository = lineaRecetaRepository;
    }

    @GetMapping("")
    public String verPedidos(
            @RequestParam(value = "selected", required = false) Integer selectedId,
            Model model) {
        
        // Bloque de seguridad: Inicializar productos de prueba si MariaDB está vacía
        List<Producto> listaProductos = productoRepository.findAll();
        if (listaProductos.isEmpty()) {
            Producto p1 = new Producto();
            p1.setNombre("Hamburguesa Gourmet");
            p1.setPrecio(25000);
            p1.setStock(50); // Añadimos stock para pasar las validaciones
            productoRepository.save(p1);

            Producto p2 = new Producto();
            p2.setNombre("Papas Fritas");
            p2.setPrecio(10000);
            p2.setStock(100);
            productoRepository.save(p2);
            
            listaProductos = productoRepository.findAll();
        }

        model.addAttribute("pedidosPendientes", pedidoRepository.findByEstadoOrderByFechaRegistroDesc("PENDIENTE"));
        model.addAttribute("pedidosEnCurso", pedidoRepository.findByEstadoOrderByFechaRegistroDesc("EN_CURSO"));
        model.addAttribute("pedidosCerrados", pedidoRepository.findByEstadoOrderByFechaRegistroDesc("CERRADO"));
        model.addAttribute("productos", listaProductos);

        Pedido selected;
        boolean orderSelected = false;
        if (selectedId != null) {
            selected = pedidoRepository.findById(selectedId).orElse(null);
            if (selected != null) {
                orderSelected = true;
                if (selected.getEstado() == null || selected.getEstado().isBlank()) {
                    selected.setEstado("PENDIENTE");
                } else {
                    selected.setEstado(selected.getEstado().toUpperCase());
                }
            } else {
                selected = new Pedido();
            }
        } else {
            selected = new Pedido();
        }
        model.addAttribute("selectedPedido", selected);
        model.addAttribute("orderSelected", orderSelected);
        return "pedido/formulario";
    }

    @PostMapping("/nuevo")
    public String nuevoPedido(@RequestParam("nombreCliente") String nombreCliente) {
        if (nombreCliente == null || nombreCliente.isBlank()) {
            return "redirect:/pedido?error=nombre";
        }
        Pedido pedido = new Pedido(nombreCliente.trim());
        pedido.setEstado("PENDIENTE");
        pedido.setFechaRegistro(LocalDateTime.now());
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return "redirect:/pedido?selected=" + pedidoGuardado.getIdPedido();
    }

    @PostMapping("/agregar")
    public String agregarProducto(
            @RequestParam("idPedido") int idPedido,
            @RequestParam("idProducto") int idProducto, // Corregido para usar ID real en vez de índice
            @RequestParam("cantidad") int cantidad) {
        if (cantidad < 1) {
            return "redirect:/pedido?error=cantidad";
        }
        Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
        if (pedido == null || !"PENDIENTE".equals(pedido.getEstado())) {
            return "redirect:/pedido?error=producto";
        }
        
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            return "redirect:/pedido?error=producto";
        }
        
        int yaEnPedido = 0;
        if (pedido.getItems() != null) {
            for (ItemPedido item : pedido.getItems()) {
                if (item.getProducto().getId().equals(producto.getId())) {
                    yaEnPedido += item.getCantidad();
                }
            }
        }
        
        if (yaEnPedido + cantidad > producto.getStock()) {
            return "redirect:/pedido?error=producto";
        }
        
        ItemPedido nuevoItem = new ItemPedido(producto, cantidad);
        nuevoItem.setPedido(pedido); // Vinculamos la relación bidireccional de JPA
        pedido.agregarItem(nuevoItem);
        
        pedidoRepository.save(pedido);
        return "redirect:/pedido?selected=" + idPedido;
    }

    @PostMapping("/cerrar")
    public String cerrarPedido(@RequestParam("idPedido") int idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
        if (pedido == null || !"PENDIENTE".equals(pedido.getEstado())) {
            return "redirect:/pedido?error=cerrar";
        }
        Map<Producto, Integer> cantidades = new HashMap<>();
        for (ItemPedido item : pedido.getItems()) {
            cantidades.merge(item.getProducto(), item.getCantidad(), Integer::sum);
        }
        for (Map.Entry<Producto, Integer> e : cantidades.entrySet()) {
            if (e.getKey().getStock() < e.getValue()) {
                return "redirect:/pedido?error=cerrar";
            }
        }
        Map<Ingrediente, Double> consumos = new HashMap<>();
        for (ItemPedido item : pedido.getItems()) {
            List<LineaReceta> recetas = lineaRecetaRepository.findByProductoOrderByIngredienteNombre(item.getProducto());
            for (LineaReceta lr : recetas) {
                double consumo = lr.getCantidadPorUnidadProducto() * item.getCantidad();
                consumos.merge(lr.getIngrediente(), consumo, Double::sum);
            }
        }
        for (Map.Entry<Ingrediente, Double> e : consumos.entrySet()) {
            if (e.getKey().getCantidadDisponible() < e.getValue()) {
                return "redirect:/pedido?error=cerrar";
            }
        }
        for (Map.Entry<Producto, Integer> e : cantidades.entrySet()) {
            Producto p = e.getKey();
            p.setStock(p.getStock() - e.getValue());
            productoRepository.save(p);
        }
        for (Map.Entry<Ingrediente, Double> e : consumos.entrySet()) {
            Ingrediente ing = e.getKey();
            ing.setCantidadDisponible(ing.getCantidadDisponible() - e.getValue());
            ingredienteRepository.save(ing);
        }
        pedido.setEstado("EN_CURSO");
        pedidoRepository.save(pedido);
        return "redirect:/pedido?selected=" + idPedido;
    }
}
