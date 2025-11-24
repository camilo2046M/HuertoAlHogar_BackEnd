package com.huertohogar.huertohogar_api.service;

import com.huertohogar.huertohogar_api.model.Usuario;
import com.huertohogar.huertohogar_api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 👇 CAMBIO DE NOMBRE: de 'registrarUsuario' a 'register'
    public Usuario register(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    // 👇 CAMBIO DE NOMBRE: de 'findUsuarioByCorreo' a 'findByCorreo'
    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }
}