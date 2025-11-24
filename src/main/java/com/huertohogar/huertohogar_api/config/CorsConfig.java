package com.huertohogar.huertohogar_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Usamos "/**" para aplicar esto a TODAS las rutas de la aplicación
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // Tu URL de React
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Incluimos OPTIONS
                .allowedHeaders("*") // Permitimos todos los encabezados (como Authorization)
                .allowCredentials(true); // Permitimos credenciales/cookies si fuera necesario
    }
}