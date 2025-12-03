package com.huertohogar.huertohogar_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pedidos") // La tabla de pedidos
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // --- Relación con Usuario ---
    // Muchos Pedidos pueden pertenecer a Un Usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // Esta es la "llave foránea"
    private Usuario usuario;

    // --- Relación con los Detalles ---
    // Un Pedido puede tener Muchos Detalles (ítems)
    // 'mappedBy = "pedido"' le dice a JPA que la relación la maneja el campo "pedido" en DetallePedido.
    // 'CascadeType.ALL' significa que si borramos un Pedido, se borran sus detalles.
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles;

    // --- Otros Campos ---
    @NotNull
    private Double total; // Guardaremos el total como número

    @NotBlank
    private String estado; // Ej: "PENDIENTE_PAGO", "PAGADO", "ENVIADO"

    @NotNull
    private LocalDate fechaCreacion;

    // Datos del formulario de checkout
    @NotBlank
    private String direccionEntrega;

    @NotBlank
    private String telefonoEntrega;

    @NotNull
    private LocalDate fechaEntregaPreferida;

    // (MercadoPago nos dará un ID de pago, lo guardaremos aquí)

}