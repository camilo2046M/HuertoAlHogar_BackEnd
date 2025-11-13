package com.huertohogar.huertohogar_api.repository;

import com.huertohogar.huertohogar_api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // (Opcional, pero útil para el "Mis Pedidos")
    // Spring creará automáticamente una consulta para buscar pedidos por el ID del usuario
    List<Pedido> findByUsuarioId(Long usuarioId);
}