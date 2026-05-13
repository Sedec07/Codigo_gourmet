package com.codigo.gourmet.controller;

import com.codigo.gourmet.model.Pedido;
import com.codigo.gourmet.service.TiendaMemoria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    private final TiendaMemoria tienda;

    public PedidoController(TiendaMemoria tienda) {
        this.tienda = tienda;
    }

    @GetMapping
    public String formulario(
            @RequestParam(value = "selected", required = false) Integer selectedId,
            Model model) {
        model.addAttribute("pedidosPendientes", tienda.listarPedidosPorEstado("PENDIENTE"));
        model.addAttribute("pedidosEnCurso", tienda.listarPedidosPorEstado("EN_CURSO"));
        model.addAttribute("pedidosCerrados", tienda.listarPedidosPorEstado("CERRADO"));
        model.addAttribute("productos", tienda.getCatalogo());

        Pedido selected = null;
        if (selectedId != null) {
            selected = tienda.buscarPedidoPorId(selectedId);
        }
        model.addAttribute("selectedPedido", selected);
        return "pedido/formulario";
    }

    @PostMapping("/nuevo")
    public String nuevoPedido(@RequestParam("nombreCliente") String nombreCliente) {
        if (nombreCliente == null || nombreCliente.isBlank()) {
            return "redirect:/pedido?error=nombre";
        }
        Pedido pedido = tienda.crearPedido(nombreCliente.trim());
        return "redirect:/pedido?selected=" + pedido.getIdPedido();
    }

    @PostMapping("/agregar")
    public String agregarProducto(
            @RequestParam("idPedido") int idPedido,
            @RequestParam("indiceProducto") int indiceProducto,
            @RequestParam("cantidad") int cantidad) {
        if (cantidad < 1) {
            return "redirect:/pedido?selected=" + idPedido + "&error=cantidad";
        }
        boolean ok = tienda.agregarItemAPedido(idPedido, indiceProducto, cantidad);
        if (!ok) {
            return "redirect:/pedido?selected=" + idPedido + "&error=producto";
        }
        return "redirect:/pedido?selected=" + idPedido;
    }

    @PostMapping("/cerrar/{id}")
    public String cerrarPedido(@PathVariable("id") int idPedido) {
        boolean ok = tienda.cerrarPedido(idPedido);
        if (!ok) {
            return "redirect:/pedido?selected=" + idPedido + "&error=cerrar";
        }
        return "redirect:/pedido";
    }
}
