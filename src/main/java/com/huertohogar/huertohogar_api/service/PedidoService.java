package com.huertohogar.huertohogar_api.service;

import com.huertohogar.huertohogar_api.dto.ItemPedidoDto;
import com.huertohogar.huertohogar_api.dto.PedidoRequestDto;
import com.huertohogar.huertohogar_api.model.DetallePedido;
import com.huertohogar.huertohogar_api.model.Pedido;
import com.huertohogar.huertohogar_api.model.Producto;
import com.huertohogar.huertohogar_api.model.Usuario;
import com.huertohogar.huertohogar_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final CarritoRepository carritoRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    // ELIMINADO: private Double getPrice(String precioString) {...}
    // Ya no es necesario porque el precio viene limpio como entero.

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository, CarritoRepository carritoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.carritoRepository = carritoRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public String crearPedidoYGenerarPago(PedidoRequestDto pedidoRequest) throws StripeException {

        // --- 1. GUARDAR EL PEDIDO ---
        Usuario usuario = usuarioRepository.findById(pedidoRequest.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUsuario(usuario);
        nuevoPedido.setDireccionEntrega(pedidoRequest.getDireccionEntrega());
        if (pedidoRequest.getTelefonoEntrega() != null && !pedidoRequest.getTelefonoEntrega().isEmpty()) {
            nuevoPedido.setTelefonoEntrega(pedidoRequest.getTelefonoEntrega());
        } else {
            nuevoPedido.setTelefonoEntrega("Sin teléfono"); // O el teléfono del perfil del usuario
        };
        if (pedidoRequest.getFechaEntregaPreferida() != null) {
            nuevoPedido.setFechaEntregaPreferida(pedidoRequest.getFechaEntregaPreferida());
        } else {
            nuevoPedido.setFechaEntregaPreferida(LocalDate.now().plusDays(3));
        };
        nuevoPedido.setFechaCreacion(LocalDate.now());
        nuevoPedido.setEstado("PENDIENTE_PAGO");

        List<DetallePedido> detalles = new ArrayList<>();
        double totalCalculado = 0.0;

        for (ItemPedidoDto itemDto : pedidoRequest.getItems()) {
            Producto producto = productoRepository.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemDto.getProductoId()));

            // CAMBIO AQUÍ: Obtenemos el int directo y lo convertimos a double para los cálculos
            int precioInt = producto.getPrecio();
            double precioReal = (double) precioInt;

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(nuevoPedido);
            detalle.setProducto(producto);
            detalle.setCantidad(itemDto.getCantidad());
            detalle.setPrecioUnitario(precioReal);
            detalles.add(detalle);

            totalCalculado += (precioReal * itemDto.getCantidad());
        }

        nuevoPedido.setTotal(totalCalculado);
        nuevoPedido.setDetalles(detalles);

        // Guardamos el pedido en la BD
        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);
        carritoRepository.deleteByUsuario(usuario);

        // --- 2. CREAR SESIÓN DE PAGO EN STRIPE ---

        // URLs a las que Stripe redirigirá al usuario
        // IMPORTANTE: Si estás probando en Android Emulator, localhost no sirve para el redirect del navegador del móvil.
        // Pero Stripe maneja la sesión en su servidor, así que esto es a dónde vuelve el usuario DESPUÉS de pagar.
        // Usa TU IP REAL de AWS (sin el puerto 9090, porque el frontend está en el 80)
        String successUrl = "http://52.44.157.216/pago-exitoso?pedido_id=" + pedidoGuardado.getId();
        String cancelUrl = "http://52.44.157.216/pago-fallido";

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (DetallePedido detalle : pedidoGuardado.getDetalles()) {
            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(Long.valueOf(detalle.getCantidad()))
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("clp") // Peso Chileno
                                            // Stripe espera Long. Como CLP no tiene decimales, pasamos el valor entero tal cual.
                                            // CORRECTO (Extraemos el valor long explícitamente)
                                            .setUnitAmount(detalle.getPrecioUnitario().longValue())
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(detalle.getProducto().getNombre())
                                                            //.addImage(detalle.getProducto().getImagenSrc())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .build();

        Session session = Session.create(params);
        System.out.println("DEBUG STRIPE URL: " + session.getUrl());
        return session.getUrl();
    }

    public List<Pedido> getPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }
}