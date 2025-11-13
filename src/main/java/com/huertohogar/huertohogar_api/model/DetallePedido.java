package com.huertohogar.huertohogar_api.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalles_pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // --- Relación con Pedido ---
    // Muchos Detalles pertenecen a Un Pedido
    @ManyToOne(fetch = FetchType.LAZY) // LAZY para no cargar el pedido entero siempre
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore // ¡Importante! Evita bucles infinitos al convertir a JSON
    private Pedido pedido;

    // --- Relación con Producto ---
    // Muchos Detalles pueden apuntar a Un Producto
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto; // Guarda el producto que se compró

    // --- Campos Adicionales ---
    @NotNull
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull
    private Double precioUnitario; // Guardamos el precio al momento de la compra
}