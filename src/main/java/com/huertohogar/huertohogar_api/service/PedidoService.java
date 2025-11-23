package com.huertohogar.huertohogar_api.service;

import com.huertohogar.huertohogar_api.dto.ItemPedidoDto;
import com.huertohogar.huertohogar_api.dto.PedidoRequestDto;
import com.huertohogar.huertohogar_api.model.DetallePedido;
import com.huertohogar.huertohogar_api.model.Pedido;
import com.huertohogar.huertohogar_api.model.Producto;
import com.huertohogar.huertohogar_api.model.Usuario;
import com.huertohogar.huertohogar_api.repository.DetallePedidoRepository;
import com.huertohogar.huertohogar_api.repository.PedidoRepository;
import com.huertohogar.huertohogar_api.repository.ProductoRepository;
import com.huertohogar.huertohogar_api.repository.UsuarioRepository;
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

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    // Función para limpiar el string de precio (la copiamos de App.jsx)
    private Double getPrice(String precioString) {
        if (precioString == null) return 0.0;
        String cleanPrice = precioString
                .replace("$", "")
                .replace(".", "") // Asumimos que . es separador de miles
                .replace(",", "."); // Asumimos que , es decimal
        return Double.parseDouble(cleanPrice.replaceAll("[^0-9.]", ""));
    }

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository, ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public String crearPedidoYGenerarPago(PedidoRequestDto pedidoRequest) throws StripeException {

        // --- 1. GUARDAR EL PEDIDO (Esto es igual que antes) ---
        Usuario usuario = usuarioRepository.findById(pedidoRequest.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setUsuario(usuario);
        nuevoPedido.setDireccionEntrega(pedidoRequest.getDireccionEntrega());
        nuevoPedido.setTelefonoEntrega(pedidoRequest.getTelefonoEntrega());
        nuevoPedido.setFechaEntregaPreferida(pedidoRequest.getFechaEntregaPreferida());
        nuevoPedido.setFechaCreacion(LocalDate.now());
        nuevoPedido.setEstado("PENDIENTE_PAGO"); // ¡Importante!

        List<DetallePedido> detalles = new ArrayList<>();
        double totalCalculado = 0.0;

        for (ItemPedidoDto itemDto : pedidoRequest.getItems()) {
            Producto producto = productoRepository.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + itemDto.getProductoId()));

            Double precioReal = getPrice(producto.getPrecio());

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

        // --- 2. CREAR SESIÓN DE PAGO EN STRIPE ---

        // URLs a las que Stripe redirigirá al usuario
        String successUrl = "http://localhost:5173/pago-exitoso?pedido_id=" + pedidoGuardado.getId();
        String cancelUrl = "http://localhost:5173/pago-fallido";

        // Convertimos los detalles del pedido al formato que Stripe entiende
        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        for (DetallePedido detalle : pedidoGuardado.getDetalles()) {
            lineItems.add(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(Long.valueOf(detalle.getCantidad()))
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("clp") // ¡Importante! Peso Chileno
                                            .setUnitAmount((long) (detalle.getPrecioUnitario() * 100)) // Stripe usa centavos
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(detalle.getProducto().getNombre())
                                                            //.addImage(detalle.getProducto().getImagenSrc()) // Opcional
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        }

        // Creamos los parámetros de la sesión de pago
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .build();

        // Creamos la sesión en Stripe
        Session session = Session.create(params);

        // Devolvemos la URL de pago
        return session.getUrl();
    }

    public List<Pedido> getPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    // (Aquí irían métodos para buscar pedidos, etc.)
}