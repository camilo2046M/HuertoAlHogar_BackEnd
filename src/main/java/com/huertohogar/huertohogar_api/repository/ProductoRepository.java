package com.huertohogar.huertohogar_api.repository;


import com.huertohogar.huertohogar_api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoriaContainingIgnoreCase(String categoria);
}