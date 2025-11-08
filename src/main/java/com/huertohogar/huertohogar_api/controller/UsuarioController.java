package com.huertohogar.huertohogar_api.controller;

import com.huertohogar.huertohogar_api.dto.LoginRequest;
import com.huertohogar.huertohogar_api.model.Usuario; // (Importa tu modelo Usuario)
import com.huertohogar.huertohogar_api.service.UsuarioService; // (Importa tu servicio Usuario)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Inyección por constructor (la mejor práctica)
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // --- Endpoint para REGISTRAR un nuevo usuario ---
    @PostMapping("/register") // Usamos /api/usuarios/register
    @Operation(summary = "Registrar un nuevo usuario")
    public Usuario registrarUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar un usuario (Iniciar Sesión)")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest loginRequest) {

        Optional<Usuario> usuarioOpt = usuarioService.loginUsuario(
                loginRequest.getCorreo(),
                loginRequest.getPassword()
        );

        if (usuarioOpt.isPresent()) {
            // Login exitoso: Devuelve 200 OK y el objeto Usuario
            // (Ocultamos la contraseña en la respuesta por seguridad)
            Usuario usuario = usuarioOpt.get();
            usuario.setPassword(null); // No enviar el hash de la contraseña al frontend
            return ResponseEntity.ok(usuario);
        } else {
            // Login fallido: Devuelve 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    // --- Manejador de Excepciones (copiado de ProductoController) ---
    // Atrapa los errores de @Valid para este controlador
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

    // (Más adelante añadiremos aquí el endpoint de Login)
}