package com.huertohogar.huertohogar_api.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.util.Date;
import java.time.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String nombre;

    @NotBlank(message = "El origen no puede estar vacío")
    private String origen;

    @NotNull(message = "El precio es obligatorio") // Valida que no sea null
    @Positive(message = "El precio debe ser mayor a 0") // Valida que sea un número positivo
    private Integer precio;

    private String imagenSrc;

    private String descripcion;

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotBlank(message = "La categoría no puede estar vacía")
    private String categoria;


}