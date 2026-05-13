package com.codigo.gourmet.controller;

import com.codigo.gourmet.model.ItemPedido;
import com.codigo.gourmet.model.Pedido;
import com.codigo.gourmet.model.Producto;
import com.codigo.gourmet.service.GuardarPedidoResultado;
import com.codigo.gourmet.service.TiendaMemoria;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    private static final String SESSION_PEDIDO_BORRADOR = "pedidoBorrador";

    private final TiendaMemoria tienda;

    public PedidoController(TiendaMemoria tienda) {
        this.tienda = tienda;
    }

    @GetMapping
    public String formulario(HttpSession session, Model model) {
        if (session.getAttribute(SESSION_PEDIDO_BORRADOR) == null) {
            session.setAttribute(SESSION_PEDIDO_BORRADOR, new Pedido(tienda.siguienteIdPedido()));
        }
        Pedido borrador = (Pedido) session.getAttribute(SESSION_PEDIDO_BORRADOR);
        List<Integer> disponibles = new ArrayList<>();
        for (int i = 0; i < tienda.tamanoCatalogo(); i++) {
            Producto pr = tienda.productoPorIndice(i);
            disponibles.add(tienda.disponibleConsiderandoBorrador(borrador, pr));
        }
        model.addAttribute("productos", tienda.getCatalogo());
        model.addAttribute("disponibles", disponibles);
        model.addAttribute("pedido", borrador);
        return "pedido/formulario";
    }

    @PostMapping("/agregar")
    public String agregar(
            @RequestParam("indiceProducto") int indiceProducto,
            @RequestParam("cantidad") int cantidad,
            HttpSession session) {
        if (cantidad < 1) {
            return "redirect:/pedido?error=cantidad";
        }
        if (indiceProducto < 0 || indiceProducto >= tienda.tamanoCatalogo()) {
            return "redirect:/pedido?error=producto";
        }
        Pedido borrador = (Pedido) session.getAttribute(SESSION_PEDIDO_BORRADOR);
        if (borrador == null) {
            return "redirect:/pedido";
        }
        Producto producto = tienda.productoPorIndice(indiceProducto);
        int yaEnPedido = tienda.cantidadDelProductoEnPedido(borrador, producto);
        if (yaEnPedido + cantidad > producto.getStock()) {
            return "redirect:/pedido?error=stock";
        }
        borrador.agregarItem(new ItemPedido(producto, cantidad));
        return "redirect:/pedido";
    }

    @PostMapping("/guardar")
    public String guardar(HttpSession session) {
        Pedido borrador = (Pedido) session.getAttribute(SESSION_PEDIDO_BORRADOR);
        if (borrador == null || borrador.getItems().isEmpty()) {
            return "redirect:/pedido?error=vacio";
        }
        GuardarPedidoResultado resultado = tienda.guardarPedido(borrador);
        if (resultado == GuardarPedidoResultado.STOCK_PRODUCTO_INSUFICIENTE) {
            return "redirect:/pedido?error=stockguardar";
        }
        if (resultado == GuardarPedidoResultado.INGREDIENTES_INSUFICIENTES) {
            return "redirect:/pedido?error=ingredientesguardar";
        }
        if (resultado != GuardarPedidoResultado.OK) {
            return "redirect:/pedido?error=stockguardar";
        }
        int id = borrador.getIdPedido();
        session.removeAttribute(SESSION_PEDIDO_BORRADOR);
        return "redirect:/pedido/resumen/" + id;
    }

    @GetMapping("/resumen/{id}")
    public String resumen(@PathVariable("id") int id, Model model) {
        Pedido pedido = tienda.buscarPedidoPorId(id);
        if (pedido == null) {
            return "redirect:/pedido?error=noexiste";
        }
        model.addAttribute("pedido", pedido);
        return "pedido/resumen";
    }
}
