package com.huertohogar.huertohogar_api.controller;

import com.huertohogar.huertohogar_api.dto.LoginRequest;
import com.huertohogar.huertohogar_api.model.Usuario;
import com.huertohogar.huertohogar_api.repository.UsuarioRepository; // Necesario para buscar el rol al loguear
import com.huertohogar.huertohogar_api.security.JwtService; // OJO: Tu JwtService debe tener el método actualizado
import com.huertohogar.huertohogar_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder; // Necesario para encriptar
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para login, registro y perfil")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository; // Agregado para buscar rápido en login
    private final PasswordEncoder passwordEncoder; // Agregado

    public AuthController(AuthenticationManager authManager, JwtService jwtService,
                          UsuarioService usuarioService, UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<?> register(@Valid @RequestBody Usuario usuario) {
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo ya está en uso"));
        }

        // 1. Asignar ROL por defecto si viene nulo
        if (usuario.getRole() == null || usuario.getRole().isEmpty()) {
            usuario.setRole("USER");
        }

        // 2. Encriptar contraseña aquí (o asegúrate que usuarioService lo haga)
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        usuarioRepository.save(usuario); // O usuarioService.register(usuario)

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
                // Buscamos al usuario para obtener su ROL real de la BD
                Usuario usuario = usuarioRepository.findByCorreo(loginRequest.getCorreo()).orElseThrow();

                // OJO: Aquí estoy asumiendo que modificaste JwtService para aceptar (correo, rol)
                // Si tu JwtService no acepta 2 parámetros, avísame.
                String token = jwtService.generateToken(usuario.getCorreo(), usuario.getRole());

                return ResponseEntity.ok(Map.of(
                        "token", token,
                        "username", usuario.getNombre(), // Es más amigable devolver el nombre real
                        "email", usuario.getCorreo(),
                        "role", usuario.getRole() // <--- IMPORTANTE PARA REACT/ANDROID
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Obtener perfil (Requiere Token)")
    public ResponseEntity<?> getPerfil(Authentication authentication) {
        // Validar que el usuario esté autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }

        // El 'name' de la autenticación es el correo (subject del token)
        String correo = authentication.getName();

        return usuarioRepository.findByCorreo(correo)
                .map(usuario -> {
                    usuario.setPassword(null); // Ocultar contraseña por seguridad
                    return ResponseEntity.ok(usuario);
                })
                .orElse(ResponseEntity.notFound().build());
    }}