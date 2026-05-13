package com.codigo.gourmet.repository;

import com.codigo.gourmet.model.LineaReceta;
import com.codigo.gourmet.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineaRecetaRepository extends JpaRepository<LineaReceta, Long> {

    List<LineaReceta> findByProductoOrderByIngredienteNombre(Producto producto);

    void deleteByProductoAndIngredienteId(Producto producto, Integer ingredienteId);
}
