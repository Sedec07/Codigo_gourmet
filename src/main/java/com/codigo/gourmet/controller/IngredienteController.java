package com.codigo.gourmet.controller;

import com.codigo.gourmet.model.UnidadMedida;
import com.codigo.gourmet.service.TiendaMemoria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IngredienteController {

    private final TiendaMemoria tienda;

    public IngredienteController(TiendaMemoria tienda) {
        this.tienda = tienda;
    }

    @GetMapping("/ingredientes")
    public String lista(Model model) {
        model.addAttribute("ingredientes", tienda.getIngredientes());
        return "ingredientes/lista";
    }

    @GetMapping("/ingredientes/nuevo")
    public String formularioNuevo() {
        return "ingredientes/formulario";
    }

    @PostMapping("/ingredientes/nuevo")
    public String registrar(
            @RequestParam("nombre") String nombre,
            @RequestParam("cantidad") double cantidad,
            @RequestParam("unidad") UnidadMedida unidad) {
        if (nombre == null || nombre.isBlank() || cantidad < 0) {
            return "redirect:/ingredientes/nuevo?error=1";
        }
        tienda.registrarIngrediente(nombre, cantidad, unidad);
        return "redirect:/ingredientes";
    }
}
