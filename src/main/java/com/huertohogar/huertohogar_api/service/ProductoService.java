package com.huertohogar.huertohogar_api.service;

import com.huertohogar.huertohogar_api.repository.ProductoRepository;
import com.huertohogar.huertohogar_api.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public Page<Producto> getAllProductos(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    // Método para obtener un producto por su ID
    public Producto getProductoById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // Método para guardar un producto (nuevo o actualizado)
    public Producto saveProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    // Método para eliminar un producto
    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> searchProductosByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> getProductosByCategoria(String categoria) {
        return productoRepository.findByCategoriaContainingIgnoreCase(categoria);
    }

}