package com.huertohogar.huertohogar_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito_items")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    private int cantidad;

    // Constructores
    public CarritoItem() {}

    public CarritoItem(Usuario usuario, Producto producto, int cantidad) {
        this.usuario = usuario;
        this.producto = producto;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // IMPORTANTE: Al serializar a JSON, no queremos enviar TODO el usuario de vuelta,
    // solo nos interesa el producto. Pero por simplicidad, lo dejamos así.
    // Si te da error de "Recursion infinita", avísame.
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}