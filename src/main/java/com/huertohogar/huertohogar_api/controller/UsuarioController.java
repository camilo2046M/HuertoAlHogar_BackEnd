package com.huertohogar.huertohogar_api.controller;

import com.huertohogar.huertohogar_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios (Rutas protegidas)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Este controlador queda limpio para futuras funcionalidades administrativas
    // o de gestión de perfil avanzada (PUT /update, etc.)
}