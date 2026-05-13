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
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping
    public String formulario(
            @RequestParam(value = "selected", required = false) Integer selectedId,
            Model model) {
        model.addAttribute("pedidosPendientes", pedidoRepository.findByEstadoOrderByFechaRegistroDesc("PENDIENTE"));
        model.addAttribute("pedidosEnCurso", pedidoRepository.findByEstadoOrderByFechaRegistroDesc("EN_CURSO"));
        model.addAttribute("pedidosCerrados", pedidoRepository.findByEstadoOrderByFechaRegistroDesc("CERRADO"));
        model.addAttribute("productos", productoRepository.findAll());

        Pedido selected = null;
        if (selectedId != null) {
            selected = pedidoRepository.findById(selectedId).orElse(null);
        }
        model.addAttribute("selectedPedido", selected);
        return "pedido/formulario";
    }

    @PostMapping("/nuevo")
    public String nuevoPedido(@RequestParam("nombreCliente") String nombreCliente) {
        if (nombreCliente == null || nombreCliente.isBlank()) {
            return "redirect:/pedido?error=nombre";
        }
        Pedido pedido = new Pedido();
        pedido.setNombreCliente(nombreCliente.trim());
        pedido.setEstado("PENDIENTE");
        pedido.setFechaRegistro(LocalDateTime.now());
        pedidoRepository.save(pedido);
        return "redirect:/pedido";
    }

    @PostMapping("/agregar")
    public String agregarProducto(
            @RequestParam("idPedido") int idPedido,
            @RequestParam("indiceProducto") int indiceProducto,
            @RequestParam("cantidad") int cantidad) {
        if (cantidad < 1) {
            return "redirect:/pedido?error=cantidad";
        }
        Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
        if (pedido == null || !"PENDIENTE".equals(pedido.getEstado())) {
            return "redirect:/pedido?error=producto";
        }
        List<Producto> productos = productoRepository.findAll();
        if (indiceProducto < 0 || indiceProducto >= productos.size()) {
            return "redirect:/pedido?error=producto";
        }
        Producto producto = productos.get(indiceProducto);
        int yaEnPedido = 0;
        for (ItemPedido item : pedido.getItems()) {
            if (item.getProducto().getId().equals(producto.getId())) {
                yaEnPedido += item.getCantidad();
            }
        }
        if (yaEnPedido + cantidad > producto.getStock()) {
            return "redirect:/pedido?error=producto";
        }
        pedido.agregarItem(new ItemPedido(producto, cantidad));
        pedidoRepository.save(pedido);
        return "redirect:/pedido";
    }

    @PostMapping("/cerrar/{id}")
    public String cerrarPedido(@PathVariable("id") int idPedido) {
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
        return "redirect:/pedido";
    }
}
