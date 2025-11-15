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
import com.huertohogar.huertohogar_api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios y autenticación (JWT)")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager; // ¡Nuevo!
    private final JwtService jwtService; // ¡Nuevo!

    public UsuarioController(UsuarioService usuarioService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public Usuario registrarUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar un usuario y generar JWT")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest loginRequest) {
        System.out.println("DEBUG: ¡El controlador de Login se ha ejecutado!");
        Authentication authentication = authenticationManager.authenticate(
                // Usa el correo como "username"
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getCorreo(),
                        loginRequest.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(loginRequest.getCorreo());

            Map<String, String> response = Map.of("token", token);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }

}