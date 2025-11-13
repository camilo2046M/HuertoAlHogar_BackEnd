package com.huertohogar.huertohogar_api.controller;

import com.huertohogar.huertohogar_api.dto.PedidoRequestDto;
import com.huertohogar.huertohogar_api.model.Pedido;
import com.huertohogar.huertohogar_api.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import com.stripe.exception.StripeException;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "API para la gestión de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    // Inyección por constructor
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // --- Endpoint para CREAR un nuevo pedido (Checkout) ---
    @PostMapping
    @Operation(summary = "Crear un nuevo pedido y obtener link de pago")
    public ResponseEntity<?> crearPedido(@Valid @RequestBody PedidoRequestDto pedidoRequest) {
        try {
            // Llama al nuevo método del servicio
            String urlDePago = pedidoService.crearPedidoYGenerarPago(pedidoRequest);

            // Creamos un JSON simple para devolver la URL
            Map<String, String> response = new HashMap<>();
            response.put("urlPago", urlDePago);

            // Devolvemos 201 CREATED y el JSON con la URL
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (StripeException e) {
            // Error de Stripe
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la sesión de pago: " + e.getMessage());
        } catch (RuntimeException e) {
            // Error nuestro (ej: Usuario no encontrado)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // (Aquí irían los endpoints para "Mis Pedidos", como GET /api/pedidos/usuario/{id})

    // --- Manejador de Excepciones (para @Valid) ---
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
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener el historial de pedidos de un usuario")
    public ResponseEntity<List<Pedido>> getPedidosDelUsuario(@PathVariable Long usuarioId) {
        List<Pedido> pedidos = pedidoService.getPedidosPorUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }
}