package com.codigo.gourmet.repository;

import com.codigo.gourmet.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}