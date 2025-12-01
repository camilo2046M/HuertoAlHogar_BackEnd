package com.huertohogar.huertohogar_api.config;

import com.huertohogar.huertohogar_api.model.Post;
import com.huertohogar.huertohogar_api.model.Producto;
import com.huertohogar.huertohogar_api.model.Usuario;
import com.huertohogar.huertohogar_api.repository.PostRepository;
import com.huertohogar.huertohogar_api.repository.ProductoRepository;
import com.huertohogar.huertohogar_api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(ProductoRepository repository, PostRepository postRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Solo precargar si la base de datos está vacía
            if (repository.count() == 0) {
                System.out.println("🌱 Sembrando datos iniciales en la base de datos...");

                List<Producto> productos = List.of(
                        // --- FRUTAS ---
                        new Producto(null, "Manzanas Fuji", "Villarica", "$2.500 / kg", "/images/manzana.jpg", "Manzana crujiente y dulce.", 100, "Frutas"),
                        new Producto(null, "Naranjas Valencia", "Concepción", "$2.200 / kg", "/images/naranja.jpg", "Jugosas y dulces.", 80, "Frutas"),
                        new Producto(null, "Plátanos Cavendish", "Santiago", "$1.900 / kg", "/images/platanos.jpg", "Ricos en potasio.", 150, "Frutas"),

                        // --- VERDURAS ---
                        new Producto(null, "Zanahorias Orgánicas", "Valparaíso", "$1.800 / kg", "/images/Zanahoria.png", "Cultivadas sin pesticidas.", 200, "Verduras"),
                        new Producto(null, "Espinacas Frescas", "Viña del Mar", "$2.000 / bolsa", "/images/espinaca.jpg", "Hojas verdes y tiernas.", 50, "Verduras"),
                        new Producto(null, "Pimientos Tricolores", "Puerto Montt", "$2.700 / bandeja", "/images/pimenton.jpg", "Rojos, verdes y amarillos.", 60, "Verduras"),

                        // --- ORGÁNICOS ---
                        new Producto(null, "Miel Orgánica", "Nacimiento", "$5.000 / frasco", "/images/miel.jpg", "Miel pura de abeja.", 40, "Organicos"),
                        new Producto(null, "Quinua Orgánica", "Villarica", "$4.200 / bolsa", "/images/quinuo.jpg", "Superalimento andino.", 100, "Organicos"),

                        // --- LÁCTEOS ---
                        new Producto(null, "Leche Entera", "Concepción", "$1.500 / litro", "/images/leche.jpg", "Leche fresca de vaca.", 120, "Lacteos")
                );

                repository.saveAll(productos);
                System.out.println("✅ Inventario cargado exitosamente.");
            }
            if (postRepository.count() == 0) {
                List<Post> posts = List.of(
                        new Post(null,
                                "Los 5 Beneficios de Comer Productos Orgánicos",
                                "Descubre cómo los alimentos orgánicos pueden mejorar tu salud...",
                                "/images/organicos.jpg",
                                "Salud",
                                "10 de Noviembre, 2025"
                        ),
                        new Post(null,
                                "Receta: Ensalada de Quínoa",
                                "Una receta fácil, rápida y deliciosa para aprovechar...",
                                "/images/ensalada.jpg",
                                "Recetas",
                                "05 de Noviembre, 2025"
                        )
                );
                postRepository.saveAll(posts);
                System.out.println("✅ Blog Posts cargados.");
            }
            if (usuarioRepository.count() == 0) {

                // Crear un ADMIN
                Usuario admin = new Usuario();
                admin.setNombre("Administrador Principal");
                admin.setCorreo("admin@huerto.cl");
                admin.setPassword(passwordEncoder.encode("admin123")); // ¡Encriptamos!
                admin.setRole("ADMIN"); // 👈 ROL IMPORTANTE
                admin.setDireccion("Oficina Central");
                admin.setTelefono("999999999");

                // Crear un USER normal (para pruebas rápidas)
                Usuario user = new Usuario();
                user.setNombre("Cliente Frecuente");
                user.setCorreo("cliente@huerto.cl");
                user.setPassword(passwordEncoder.encode("cliente123"));
                user.setRole("USER");

                usuarioRepository.saveAll(List.of(admin, user));
                System.out.println("✅ Usuarios de prueba cargados (Admin y Cliente).");
            }
        };
    }
}