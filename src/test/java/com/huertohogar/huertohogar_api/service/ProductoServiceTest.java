package com.huertohogar.huertohogar_api.service;

import com.huertohogar.huertohogar_api.model.Producto;
import com.huertohogar.huertohogar_api.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Activa Mockito
class ProductoServiceTest {

    @Mock // 1. Crea una versión "falsa" (mock) del repositorio
    private ProductoRepository productoRepository;

    @InjectMocks // 2. Crea una instancia real del servicio e inyecta los mocks
    private ProductoService productoService;

    private Producto producto1;
    private Producto producto2;

    @BeforeEach // 3. (Opcional) Se ejecuta antes de CADA prueba
    void setUp() {
        // Preparamos datos de prueba
        producto1 = new Producto(1L, "Manzana", "Villarica", "$2.500 / kg", "/img/manzana.jpg", "Rica", 100);
        producto2 = new Producto(2L, "Miel", "Nacimiento", "$5.000 / frasco", "/img/miel.jpg", "Dulce", 50);
    }

    // --- Prueba para getAllProductos (sin paginación) ---
    // Nota: Dejaremos la prueba de paginación para después, ya que es más compleja.
    // Vamos a probar primero la búsqueda por nombre, que es más directa.

    // --- Prueba para searchProductosByNombre ---
    @Test
    void testSearchProductosByNombre() {
        // 1. Arrange (Preparar)
        // Definimos lo que debe devolver el mock cuando lo llamen
        List<Producto> listaSimulada = Arrays.asList(producto1);
        when(productoRepository.findByNombreContainingIgnoreCase("Manzana")).thenReturn(listaSimulada);

        // 2. Act (Actuar)
        List<Producto> resultado = productoService.searchProductosByNombre("Manzana");

        // 3. Assert (Verificar)
        assertNotNull(resultado); // Verifica que la lista no sea nula
        assertEquals(1, resultado.size()); // Verifica que tenga 1 elemento
        assertEquals("Manzana", resultado.get(0).getNombre()); // Verifica que sea el correcto

        // Verifica que el repositorio fue llamado exactamente 1 vez
        verify(productoRepository, times(1)).findByNombreContainingIgnoreCase("Manzana");
    }

    // --- Prueba para getProductoById ---
    @Test
    void testGetProductoById() {
        // 1. Arrange
        // (Optional.of() es la forma correcta de devolver un objeto encontrado)
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto1));

        // 2. Act
        Producto resultado = productoService.getProductoById(1L);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals("Manzana", resultado.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    // --- Prueba para getProductoById (No encontrado) ---
    @Test
    void testGetProductoById_NotFound() {
        // 1. Arrange
        // (Optional.empty() es lo que devuelve si no encuentra nada)
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Act
        Producto resultado = productoService.getProductoById(99L);

        // 3. Assert
        assertNull(resultado); // El servicio debe devolver null
        verify(productoRepository, times(1)).findById(99L);
    }

    // --- Prueba para saveProducto ---
    @Test
    void testSaveProducto() {
        // 1. Arrange
        // Cuando el repositorio guarde CUALQUIER producto, que devuelva ese mismo producto
        when(productoRepository.save(any(Producto.class))).thenReturn(producto1);

        // 2. Act
        Producto resultado = productoService.saveProducto(producto1);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals("Manzana", resultado.getNombre());
        verify(productoRepository, times(1)).save(producto1);
    }

    // --- Prueba para deleteProducto ---
    @Test
    void testDeleteProducto() {
        // 1. Arrange
        // (Los métodos 'void' no devuelven nada, así que no usamos 'when')
        Long idParaBorrar = 1L;

        // 2. Act
        productoService.deleteProducto(idParaBorrar);

        // 3. Assert
        // Solo verificamos que el método deleteById fue llamado con el ID correcto
        verify(productoRepository, times(1)).deleteById(idParaBorrar);
    }
}