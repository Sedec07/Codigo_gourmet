package com.codigo.gourmet.repository;

import com.codigo.gourmet.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
}