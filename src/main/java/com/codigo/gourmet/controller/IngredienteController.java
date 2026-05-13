package com.codigo.gourmet.controller;

import com.codigo.gourmet.model.Ingrediente;
import com.codigo.gourmet.model.UnidadMedida;
import com.codigo.gourmet.repository.IngredienteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IngredienteController {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteController(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @GetMapping("/ingredientes")
    public String lista(Model model) {
        model.addAttribute("ingredientes", ingredienteRepository.findAll());
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
            @RequestParam("costo") double costo,
            @RequestParam("unidad") UnidadMedida unidad) {
        if (nombre == null || nombre.isBlank() || cantidad < 0 || costo < 0) {
            return "redirect:/ingredientes/nuevo?error=1";
        }
        Ingrediente ing = new Ingrediente(nombre.trim(), cantidad, costo, unidad);
        ingredienteRepository.save(ing);
        return "redirect:/ingredientes";
    }
}
