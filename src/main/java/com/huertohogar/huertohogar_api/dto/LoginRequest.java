package com.huertohogar.huertohogar_api.dto;

import lombok.Data;

@Data // De Lombok: crea getters y setters
public class LoginRequest {
    private String correo;
    private String password;
}