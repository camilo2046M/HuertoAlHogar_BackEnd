package com.huertohogar.huertohogar_api.service;

import com.huertohogar.huertohogar_api.model.Usuario; // (Importa tu modelo Usuario)
import com.huertohogar.huertohogar_api.repository.UsuarioRepository; // (Importa tu repo Usuario)
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        String passwordHasheada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(passwordHasheada);

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> loginUsuario(String correo, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();
        System.out.println("DEBUG PASS: RAW: " + password);
        System.out.println("DEBUG PASS: HASHED DB: " + usuario.getPassword());
        if (passwordEncoder.matches(password, usuario.getPassword())) {
            return usuarioOpt;
        } else {

            return Optional.empty();
        }
    }


    public Optional<Usuario> findUsuarioByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

}