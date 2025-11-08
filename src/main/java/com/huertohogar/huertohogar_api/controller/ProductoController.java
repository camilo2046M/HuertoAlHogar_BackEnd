package com.huertohogar.huertohogar_api.controller;

import com.huertohogar.huertohogar_api.model.Producto;
import com.huertohogar.huertohogar_api.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "API para la gestión de productos")
public class ProductoController {

    private final ProductoService productoService;


    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Obtener una lista paginada de todos los productos")
    public Page<Producto> getAllProductos(Pageable pageable) {

        return productoService.getAllProductos(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto específico por su ID")
    public Producto getProductoById(@PathVariable Long id) {
        return productoService.getProductoById(id);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo producto")
    public Producto createProducto(@Valid @RequestBody Producto producto) {
        return productoService.saveProducto(producto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un producto existente por su ID")
    public Producto updateProducto(@PathVariable Long id, @Valid @RequestBody Producto productoDetails) {
        Producto producto = productoService.getProductoById(id);

        if (producto != null) {
            producto.setNombre(productoDetails.getNombre());
            producto.setOrigen(productoDetails.getOrigen());
            producto.setPrecio(productoDetails.getPrecio());
            producto.setImagenSrc(productoDetails.getImagenSrc());
            producto.setDescripcion(productoDetails.getDescripcion());
            producto.setStock(productoDetails.getStock());
            return productoService.saveProducto(producto);
        }
        return null; //
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto por su ID")
    public void deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Obtener productos por categoría")
    public List<Producto> getProductosPorCategoria(@PathVariable String categoria) {
        return productoService.getProductosByCategoria(categoria);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);

        return body;
    }
}

