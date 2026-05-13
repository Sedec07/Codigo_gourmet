package com.codigo.gourmet.config;

import com.codigo.gourmet.model.Ingrediente;
import com.codigo.gourmet.model.Producto;
import com.codigo.gourmet.model.UnidadMedida;
import com.codigo.gourmet.repository.IngredienteRepository;
import com.codigo.gourmet.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository productoRepository;
    private final IngredienteRepository ingredienteRepository;

    public DataInitializer(ProductoRepository productoRepository, IngredienteRepository ingredienteRepository) {
        this.productoRepository = productoRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    @Override
    public void run(String... args) {
        if (productoRepository.count() == 0) {
            productoRepository.save(new Producto("Hamburguesa doble", 18000, 10));
            productoRepository.save(new Producto("Malteada vainilla", 7500, 15));
            productoRepository.save(new Producto("Papas medianas", 5000, 20));
        }
        if (ingredienteRepository.count() == 0) {
            ingredienteRepository.save(new Ingrediente("Carne molida", 5000, UnidadMedida.GRAMOS));
            ingredienteRepository.save(new Ingrediente("Pan de hamburguesa", 80, UnidadMedida.UNIDADES));
            ingredienteRepository.save(new Ingrediente("Leche", 10000, UnidadMedida.ML));
            ingredienteRepository.save(new Ingrediente("Papa cruda", 30000, UnidadMedida.GRAMOS));
        }
    }
}
