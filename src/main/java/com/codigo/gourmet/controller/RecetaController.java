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
            @RequestParam(value = "productoId", required = false) String productoIdParam,
            Model model) {
        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("productos", productos);
        model.addAttribute("ingredientes", ingredienteRepository.findAll());

        Producto seleccionado;
        boolean esNuevo = productoIdParam == null || productoIdParam.isBlank() || "nuevo".equals(productoIdParam);

        if (esNuevo) {
            seleccionado = new Producto();
        } else {
            try {
                int id = Integer.parseInt(productoIdParam);
                seleccionado = productoRepository.findById(id).orElse(productos.isEmpty() ? new Producto() : productos.get(0));
            } catch (NumberFormatException e) {
                seleccionado = new Producto();
            }
        }
        model.addAttribute("productoSeleccionado", seleccionado);

        if (seleccionado.getId() != null) {
            List<LineaReceta> lineas = lineaRecetaRepository.findByProductoOrderByIngredienteNombre(seleccionado);
            model.addAttribute("lineasReceta", lineas);
            double costoTotal = lineas.stream()
                    .mapToDouble(LineaReceta::getCosto)
                    .sum();
            model.addAttribute("costoTotalReceta", costoTotal);
        }
        return "recetas/asignar";
    }

    @PostMapping("/recetas/agregar")
public String agregarLinea(
        @RequestParam("productoId") int productoId,
        @RequestParam("nombreNuevo") String nombre, // Captura el nombre del nuevo input desbloqueado
        @RequestParam("precio") double precio,     // Captura el precio del input numérico
        @RequestParam("idIngrediente") int idIngrediente,
        @RequestParam("cantidadPorUnidad") double cantidadPorUnidad) {
    
    // Aquí tu lógica existente para guardar el producto, la receta y reorientar el flujo

        if (ingredienteRepository.findById(idIngrediente).isEmpty() || cantidadPorUnidad <= 0) {
            return "redirect:/recetas?productoId=" + productoId + "&error=1";
        }
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null) {
            return "redirect:/recetas?error=1";
        }
        List<LineaReceta> existentes = lineaRecetaRepository.findByProductoOrderByIngredienteNombre(producto);
        for (LineaReceta lr : existentes) {
            if (lr.getIngrediente().getId() == idIngrediente) {
                lr.setCantidadPorUnidadProducto(lr.getCantidadPorUnidadProducto() + cantidadPorUnidad);
                lineaRecetaRepository.save(lr);
                return "redirect:/recetas?productoId=" + productoId;
            }
        }
        LineaReceta linea = new LineaReceta();
        linea.setProducto(producto);
        linea.setIngrediente(ingredienteRepository.findById(idIngrediente).get());
        linea.setCantidadPorUnidadProducto(cantidadPorUnidad);
        lineaRecetaRepository.save(linea);
        return "redirect:/recetas?productoId=" + productoId;
    }

    @PostMapping("/recetas/quitar")
    public String quitarLinea(
            @RequestParam("productoId") int productoId,
            @RequestParam("idIngrediente") int idIngrediente) {
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto != null) {
            lineaRecetaRepository.deleteByProductoAndIngredienteId(producto, idIngrediente);
        }
        return "redirect:/recetas?productoId=" + productoId;
    }

    @PostMapping("/recetas/actualizar")
    public String actualizarCantidad(
            @RequestParam("productoId") int productoId,
            @RequestParam("idIngrediente") int idIngrediente,
            @RequestParam("cantidad") double cantidad) {
        if (cantidad <= 0) {
            return "redirect:/recetas?productoId=" + productoId + "&error=1";
        }
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto != null) {
            List<LineaReceta> lineas = lineaRecetaRepository.findByProductoOrderByIngredienteNombre(producto);
            for (LineaReceta lr : lineas) {
                if (lr.getIngrediente().getId() == idIngrediente) {
                    lr.setCantidadPorUnidadProducto(cantidad);
                    lineaRecetaRepository.save(lr);
                    break;
                }
            }
        }
        return "redirect:/recetas?productoId=" + productoId;
    }

    @PostMapping("/recetas/guardar")
public String guardarProducto(
        @RequestParam(value = "idProducto", required = false, defaultValue = "0") int idProducto,
        @RequestParam("nombreNuevo") String nombre,
        @RequestParam("precio") double precio) {
    
    if (nombre == null || nombre.isBlank()) {
        return "redirect:/recetas?error=nombre";
    }

    Producto producto;
    if (idProducto == 0) {
        // Si el ID es 0, es un producto completamente nuevo en MariaDB
        producto = new Producto();
    } else {
        // Si el ID existe, lo buscamos para actualizarlo
        producto = productoRepository.findById(idProducto).orElse(new Producto());
    }

    producto.setNombre(nombre.trim());
    producto.setPrecio(precio);
    if (producto.getStock() == 0) {
        producto.setStock(100); // Le asignamos un stock inicial de prueba
    }

    Producto productoGuardado = productoRepository.save(producto);

    // Redireccionamos a la misma pantalla seleccionando el producto recién creado
    return "redirect:/recetas?productoId=" + productoGuardado.getId();
}

}
