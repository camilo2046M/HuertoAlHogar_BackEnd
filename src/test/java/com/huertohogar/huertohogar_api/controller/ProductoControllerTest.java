package com.huertohogar.huertohogar_api.controller;

import com.huertohogar.huertohogar_api.model.Producto;
import com.huertohogar.huertohogar_api.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import java.util.Arrays;
import java.util.List;

// Importa los métodos estáticos de Mockito y Spring MVC
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*; // Para 'is' y 'hasSize'

@WebMvcTest(controllers = ProductoController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        // ✅ CORRECTO (8 argumentos, con la categoría al final)
        producto1 = new Producto(1L, "Manzana", "Villarica", "$2.500 / kg", "/img/manzana.jpg", "Rica", 100, "Frutas");
        producto2 = new Producto(2L, "Miel", "Nacimiento", "$5.000 / frasco", "/img/miel.jpg", "Dulce", 50, "Organicos");
    }

    // --- Prueba para GET /api/productos (con paginación) ---
    @Test
    void testGetAllProductos() throws Exception {
        // 1. Arrange (Preparar)
        // Simulamos la respuesta de la paginación del servicio
        List<Producto> lista = Arrays.asList(producto1, producto2);
        Page<Producto> paginaSimulada = new PageImpl<>(lista, PageRequest.of(0, 10), lista.size());

        when(productoService.getAllProductos(any(Pageable.class))).thenReturn(paginaSimulada);

        // 2. Act (Actuar) y 3. Assert (Verificar)
        mockMvc.perform(get("/api/productos") // Simula un GET a esta URL
                        .param("page", "0") // Añade parámetros URL
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verifica que la respuesta sea HTTP 200 OK
                .andExpect(jsonPath("$.content", hasSize(2))) // Verifica que el array 'content' tenga 2 elementos
                .andExpect(jsonPath("$.content[0].nombre", is("Manzana"))) // Verifica el nombre del primer producto
                .andExpect(jsonPath("$.totalElements", is(2))); // Verifica el total de elementos
    }

    // --- Prueba para GET /api/productos/{id} ---
    @Test
    void testGetProductoById() throws Exception {
        // 1. Arrange
        when(productoService.getProductoById(1L)).thenReturn(producto1);

        // 2. Act y 3. Assert
        mockMvc.perform(get("/api/productos/{id}", 1L) // Simula GET a /api/productos/1
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Manzana")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    // --- Prueba para POST /api/productos (Validación Falla) ---
    @Test
    void testCreateProducto_ValidacionFalla() throws Exception {
        // 1. Arrange
        // (No necesitamos 'when' porque la validación ocurre ANTES de llamar al servicio)

        // Creamos un JSON inválido (nombre vacío)
        String productoJsonInvalido = "{\"nombre\":\"\", \"origen\":\"Test\", \"precio\":\"$1\", \"stock\":1}";

        // 2. Act y 3. Assert
        mockMvc.perform(post("/api/productos") // Simula un POST
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productoJsonInvalido)) // Envía el JSON inválido
                .andExpect(status().isBadRequest()) // Verifica que devuelva HTTP 400
                .andExpect(jsonPath("$.errors.nombre", is("El nombre debe tener al menos 3 caracteres"))); // Verifica el mensaje de error
    }
}