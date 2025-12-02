package com.huertohogar.huertohogar_api.controller;

import com.huertohogar.huertohogar_api.dto.ItemRequest;
import com.huertohogar.huertohogar_api.model.CarritoItem;
import com.huertohogar.huertohogar_api.model.Producto;
import com.huertohogar.huertohogar_api.model.Usuario;
import com.huertohogar.huertohogar_api.repository.CarritoRepository;
import com.huertohogar.huertohogar_api.repository.ProductoRepository;
import com.huertohogar.huertohogar_api.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoRepository carritoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ProductoRepository productoRepo;

    public CarritoController(CarritoRepository carritoRepo, UsuarioRepository usuarioRepo, ProductoRepository productoRepo) {
        this.carritoRepo = carritoRepo;
        this.usuarioRepo = usuarioRepo;
        this.productoRepo = productoRepo;
    }

    // OBTENER CARRITO
    @GetMapping
    public ResponseEntity<List<CarritoItem>> obtenerCarrito(Authentication auth) {
        Usuario usuario = usuarioRepo.findByCorreo(auth.getName()).orElseThrow();
        return ResponseEntity.ok(carritoRepo.findByUsuario(usuario));
    }

    // AGREGAR O ACTUALIZAR ITEM
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarItem(@RequestBody ItemRequest request, Authentication auth) {
        Usuario usuario = usuarioRepo.findByCorreo(auth.getName()).orElseThrow();
        Producto producto = productoRepo.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar si ya existe en el carrito
        CarritoItem item = carritoRepo.findByUsuarioAndProducto(usuario, producto)
                .orElse(null);

        if (item != null) {
            // Si existe, sumamos la cantidad (o actualizamos si prefieres reemplazar)
            item.setCantidad(item.getCantidad() + request.getCantidad());
            carritoRepo.save(item);
        } else {
            // Si no existe, creamos uno nuevo
            item = new CarritoItem(usuario, producto, request.getCantidad());
            carritoRepo.save(item);
        }

        return ResponseEntity.ok(Map.of("message", "Producto agregado"));
    }

    // ELIMINAR ITEM
    @DeleteMapping("/{productoId}")
    @Transactional // Necesario para borrar
    public ResponseEntity<?> eliminarItem(@PathVariable Long productoId, Authentication auth) {
        Usuario usuario = usuarioRepo.findByCorreo(auth.getName()).orElseThrow();
        Producto producto = productoRepo.findById(productoId).orElseThrow();

        CarritoItem item = carritoRepo.findByUsuarioAndProducto(usuario, producto)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en carrito"));

        carritoRepo.delete(item);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado"));
    }
}