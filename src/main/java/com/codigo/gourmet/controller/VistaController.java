package com.codigo.gourmet.controller;

import com.codigo.gourmet.repository.PedidoRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class VistaController {

    private final PedidoRepository pedidoRepository;

    public VistaController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

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
    public String historialVentas(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAllByOrderByFechaRegistroDesc());

        LocalDate hoy = LocalDate.now();
        double totalVendidoHoy = pedidoRepository.findByFechaRegistroBetween(
                hoy.atStartOfDay(), hoy.atTime(LocalTime.MAX))
                .stream().mapToDouble(p -> p.getTotal()).sum();
        model.addAttribute("totalVendidoHoy", totalVendidoHoy);
        return "ventas/historial";
    }
}
