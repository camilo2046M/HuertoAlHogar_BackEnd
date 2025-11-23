package com.huertohogar.huertohogar_api.controller;

import com.huertohogar.huertohogar_api.dto.LoginRequest;
import com.huertohogar.huertohogar_api.model.Usuario;
import com.huertohogar.huertohogar_api.security.JwtService;
import com.huertohogar.huertohogar_api.service.CustomUserDetailsService;
import com.huertohogar.huertohogar_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth") // ✅ Coincide con AuthService.js del frontend
@Tag(name = "Autenticación", description = "Endpoints para login, registro y perfil")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authManager, JwtService jwtService, UsuarioService usuarioService, CustomUserDetailsService userDetailsService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<?> register(@Valid @RequestBody Usuario usuario) {
        if (usuarioService.findByCorreo(usuario.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está en uso"));
        }
        usuarioService.register(usuario);
        return ResponseEntity.ok(Map.of("message", "Usuario registrado correctamente"));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getCorreo(),
                            loginRequest.getPassword()
                    )
            );

            if (auth.isAuthenticated()) {
                String token = jwtService.generateToken(loginRequest.getCorreo());
                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "username", loginRequest.getCorreo()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Obtener perfil del usuario logueado")
    public ResponseEntity<?> getPerfil(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Buscamos usando el servicio que busca por correo
        // Nota: CustomUserDetailsService devuelve UserDetails, necesitamos el modelo Usuario completo
        // así que usaremos UsuarioService o el repositorio directamente si es necesario,
        // pero userDetailsService.loadUserByUsername usa el repo por debajo.

        // Mejor práctica: Buscar el usuario de dominio
        return usuarioService.findByCorreo(userDetails.getUsername())
                .map(usuario -> {
                    usuario.setPassword(null); // Ocultar password
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}