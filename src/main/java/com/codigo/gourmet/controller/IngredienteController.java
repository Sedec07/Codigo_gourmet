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

    @GetMapping("/ingredientes")
    public String lista(Model model, TiendaMemoria tienda) {
        System.out.println("[IngredienteController] GET lista tienda=" + System.identityHashCode(tienda));
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
            @RequestParam("unidad") UnidadMedida unidad,
            TiendaMemoria tienda) {
        if (nombre == null || nombre.isBlank() || cantidad < 0) {
            return "redirect:/ingredientes/nuevo?error=1";
        }
        System.out.println("[IngredienteController] POST nuevo tienda=" + System.identityHashCode(tienda)
                + " nombre=" + nombre + " cantidad=" + cantidad + " unidad=" + unidad);
        tienda.registrarIngrediente(nombre, cantidad, unidad);
        return "redirect:/ingredientes";
    }
}
