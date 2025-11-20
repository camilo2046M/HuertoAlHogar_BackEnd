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
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Define qué rutas deben ser ignoradas por este filtro.
     * Si devuelve 'true', el filtro se salta automáticamente para esa URL.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Excluir rutas públicas que ya están en SecurityConfig.permitAll()
        return path.startsWith("/api/usuarios/login") ||
                path.startsWith("/api/usuarios/register") ||
                path.startsWith("/api/productos") ||
                path.startsWith("/h2-console") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userCorreo;

        // Si shouldNotFilter devuelve 'true', esta parte del código se salta automáticamente.

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
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Crear objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}