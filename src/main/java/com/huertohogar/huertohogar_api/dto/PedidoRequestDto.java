package com.huertohogar.huertohogar_api.dto;



import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PedidoRequestDto {
    private Long usuarioId; // Quién está comprando
    private List<ItemPedidoDto> items; // El carrito

    // Datos del formulario de checkout
    private String direccionEntrega;
    private String telefonoEntrega ;
    private LocalDate fechaEntregaPreferida;
}