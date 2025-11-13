package com.huertohogar.huertohogar_api.repository;

import com.huertohogar.huertohogar_api.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    // Por ahora, no necesitamos métodos personalizados aquí.
    // JpaRepository nos da todo lo necesario.
}