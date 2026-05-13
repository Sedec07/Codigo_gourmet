package com.codigo.gourmet.controller;

import com.codigo.gourmet.model.LineaReceta;
import com.codigo.gourmet.model.Producto;
import com.codigo.gourmet.repository.IngredienteRepository;
import com.codigo.gourmet.repository.LineaRecetaRepository;
import com.codigo.gourmet.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class RecetaController {

    private final ProductoRepository productoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final LineaRecetaRepository lineaRecetaRepository;

    public RecetaController(ProductoRepository productoRepository,
                            IngredienteRepository ingredienteRepository,
                            LineaRecetaRepository lineaRecetaRepository) {
        this.productoRepository = productoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.lineaRecetaRepository = lineaRecetaRepository;
    }

    @GetMapping("/recetas")
    public String asignar(
            @RequestParam(value = "indiceProducto", required = false) Integer indiceProducto,
            Model model) {
        List<Producto> productos = productoRepository.findAll();
        int indice = (indiceProducto == null || indiceProducto < 0 || indiceProducto >= productos.size()) ? 0 : indiceProducto;
        Producto seleccionado = productos.get(indice);
        model.addAttribute("productos", productos);
        model.addAttribute("ingredientes", ingredienteRepository.findAll());
        model.addAttribute("indiceSeleccionado", indice);
        model.addAttribute("productoSeleccionado", seleccionado);
        model.addAttribute("lineasReceta", lineaRecetaRepository.findByProductoOrderByIngredienteNombre(seleccionado));
        return "recetas/asignar";
    }

    @PostMapping("/recetas/agregar")
    public String agregarLinea(
            @RequestParam("indiceProducto") int indiceProducto,
            @RequestParam("idIngrediente") int idIngrediente,
            @RequestParam("cantidadPorUnidad") double cantidadPorUnidad) {
        List<Producto> productos = productoRepository.findAll();
        if (indiceProducto < 0 || indiceProducto >= productos.size()
                || ingredienteRepository.findById(idIngrediente).isEmpty()
                || cantidadPorUnidad <= 0) {
            int safe = (indiceProducto >= 0 && indiceProducto < productos.size()) ? indiceProducto : 0;
            return "redirect:/recetas?indiceProducto=" + safe + "&error=1";
        }
        Producto producto = productos.get(indiceProducto);
        List<LineaReceta> existentes = lineaRecetaRepository.findByProductoOrderByIngredienteNombre(producto);
        for (LineaReceta lr : existentes) {
            if (lr.getIngrediente().getId() == idIngrediente) {
                lr.setCantidadPorUnidadProducto(lr.getCantidadPorUnidadProducto() + cantidadPorUnidad);
                lineaRecetaRepository.save(lr);
                return "redirect:/recetas?indiceProducto=" + indiceProducto;
            }
        }
        LineaReceta linea = new LineaReceta();
        linea.setProducto(producto);
        linea.setIngrediente(ingredienteRepository.findById(idIngrediente).get());
        linea.setCantidadPorUnidadProducto(cantidadPorUnidad);
        lineaRecetaRepository.save(linea);
        return "redirect:/recetas?indiceProducto=" + indiceProducto;
    }

    @PostMapping("/recetas/quitar")
    public String quitarLinea(
            @RequestParam("indiceProducto") int indiceProducto,
            @RequestParam("idIngrediente") int idIngrediente) {
        List<Producto> productos = productoRepository.findAll();
        if (indiceProducto >= 0 && indiceProducto < productos.size()) {
            lineaRecetaRepository.deleteByProductoAndIngredienteId(productos.get(indiceProducto), idIngrediente);
        }
        return "redirect:/recetas?indiceProducto=" + indiceProducto;
    }
}
