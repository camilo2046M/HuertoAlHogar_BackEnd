package com.huertohogar.huertohogar_api.repository;

import com.huertohogar.huertohogar_api.model.CarritoItem;
import com.huertohogar.huertohogar_api.model.Usuario;
import com.huertohogar.huertohogar_api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findByUsuario(Usuario usuario);
    Optional<CarritoItem> findByUsuarioAndProducto(Usuario usuario, Producto producto);
    void deleteByUsuario(Usuario usuario);
}