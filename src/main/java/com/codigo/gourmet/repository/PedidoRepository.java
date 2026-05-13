package com.codigo.gourmet.repository;

import com.codigo.gourmet.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByEstadoOrderByFechaRegistroDesc(String estado);

    List<Pedido> findAllByOrderByFechaRegistroDesc();

    List<Pedido> findByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);
}
