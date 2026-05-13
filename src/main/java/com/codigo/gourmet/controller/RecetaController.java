package com.codigo.gourmet.controller;

import com.codigo.gourmet.model.Producto;
import com.codigo.gourmet.service.TiendaMemoria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecetaController {

    @GetMapping("/recetas")
    public String asignar(
            @RequestParam(value = "indiceProducto", required = false) Integer indiceProducto,
            Model model,
            TiendaMemoria tienda) {
        int indice = indiceProducto == null ? 0 : indiceProducto;
        if (indice < 0 || indice >= tienda.tamanoCatalogo()) {
            indice = 0;
        }
        Producto seleccionado = tienda.productoPorIndice(indice);
        System.out.println("[RecetaController] GET recetas tienda=" + System.identityHashCode(tienda)
                + " indice=" + indice + " producto=" + seleccionado.getNombre()
                + " hashProducto=" + System.identityHashCode(seleccionado));
        model.addAttribute("productos", tienda.getCatalogo());
        model.addAttribute("ingredientes", tienda.getIngredientes());
        model.addAttribute("indiceSeleccionado", indice);
        model.addAttribute("productoSeleccionado", seleccionado);
        model.addAttribute("lineasReceta", tienda.lineasRecetaDeProducto(seleccionado));
        return "recetas/asignar";
    }

    @PostMapping("/recetas/agregar")
    public String agregarLinea(
            @RequestParam("indiceProducto") int indiceProducto,
            @RequestParam("idIngrediente") int idIngrediente,
            @RequestParam("cantidadPorUnidad") double cantidadPorUnidad,
            TiendaMemoria tienda) {
        if (indiceProducto < 0 || indiceProducto >= tienda.tamanoCatalogo()
                || tienda.buscarIngredientePorId(idIngrediente) == null
                || cantidadPorUnidad <= 0) {
            int safe = (indiceProducto >= 0 && indiceProducto < tienda.tamanoCatalogo()) ? indiceProducto : 0;
            return "redirect:/recetas?indiceProducto=" + safe + "&error=1";
        }
        tienda.agregarLineaReceta(indiceProducto, idIngrediente, cantidadPorUnidad);
        System.out.println("[RecetaController] POST agregar ok indiceProducto=" + indiceProducto
                + " idIngrediente=" + idIngrediente + " cantidadPorUnidad=" + cantidadPorUnidad);
        return "redirect:/recetas?indiceProducto=" + indiceProducto;
    }

    @PostMapping("/recetas/quitar")
    public String quitarLinea(
            @RequestParam("indiceProducto") int indiceProducto,
            @RequestParam("idIngrediente") int idIngrediente,
            TiendaMemoria tienda) {
        if (indiceProducto >= 0 && indiceProducto < tienda.tamanoCatalogo()) {
            tienda.quitarLineaRecetaIngrediente(indiceProducto, idIngrediente);
        }
        return "redirect:/recetas?indiceProducto=" + indiceProducto;
    }
}
