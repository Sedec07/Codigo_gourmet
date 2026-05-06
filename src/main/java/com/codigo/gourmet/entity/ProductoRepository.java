package com.codigo.gourmet.repository;

import com.codigo.gourmet.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}