package com.huertohogar.huertohogar_api.security;
import com.huertohogar.huertohogar_api.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Usaremos tu nuevo CustomUserDetailsService

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userCorreo;

        // 1. Verificar si hay token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer token
        jwt = authHeader.substring(7);
        userCorreo = jwtService.extractUserCorreo(jwt); // El correo es el identificador

        // 3. Autenticar si el correo es válido y no hay autenticación previa
        if (userCorreo != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar los detalles del usuario
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userCorreo);

            // 4. Validar token
            System.out.println("DEBUG: Correo extraído del token: " + userCorreo); // <-- Pista 1

            if (jwtService.isTokenValid(jwt, userDetails)) {
                System.out.println("DEBUG: ✅ TOKEN VÁLIDO. Continúa la ejecución."); // <-- Pista 2

                // ... (restablecer el contexto de seguridad) ...
            } else {
                System.out.println("DEBUG: ❌ TOKEN INVÁLIDO. FALLA LA VALIDACIÓN."); // <-- Pista 3
            }
        }

        filterChain.doFilter(request, response);
    }
}