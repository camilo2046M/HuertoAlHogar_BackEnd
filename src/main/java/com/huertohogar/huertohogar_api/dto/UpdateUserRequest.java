package com.huertohogar.huertohogar_api.dto;

import lombok.Data; // Importar Lombok

@Data // Genera Getters, Setters, toString, etc. automáticamente
public class UpdateUserRequest {
    private String nombre;
    private String direccion;
    private String imagenSrc;
}