package com.codigo.gourmet.controller;

import com.codigo.gourmet.service.TiendaMemoria;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaController {

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/ventas/historial")
    public String historialVentas(Model model, TiendaMemoria tienda) {
        model.addAttribute("pedidos", tienda.listarPedidosMasRecientesPrimero());
        model.addAttribute("totalVendidoHoy", tienda.totalVendidoHoy());
        return "ventas/historial";
    }
}