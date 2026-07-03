package com.sigepid.order.application.service;

import com.sigepid.order.application.dto.*;
import com.sigepid.order.domain.entity.Order;
import com.sigepid.order.domain.entity.OrderItem;
import com.sigepid.order.domain.enums.OrderStatus;
import com.sigepid.order.domain.repository.OrderRepository;
import com.sigepid.order.infrastructure.client.CatalogClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sigepid.order.infrastructure.client.NotificationClient;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    /** Repositorio JPA para operaciones de persistencia de pedidos. */
    private final OrderRepository orderRepository;

    /** Cliente Feign para comunicarse con el microservicio de catálogo (reducción de stock). */
    private final CatalogClient catalogClient;
    private final NotificationClient notificationClient;

    /**
     * Crea un nuevo pedido en el sistema.
     *
     * Flujo:
     * 1. Construye la entidad Order con los datos del request (usuario, dirección, estado PENDING).
     * 2. Mapea cada ítem del request a una entidad OrderItem, calculando el subtotal de cada uno.
     * 3. Asocia los ítems al pedido y calcula el monto total sumando todos los subtotales.
     * 4. Llama al catalog-service vía Feign para reducir el stock de los productos.
     * 5. Guarda el pedido en la base de datos.
     * 6. Retorna la respuesta mapeada como OrderResponse.
     *
     * @param request Datos del pedido a crear (usuario, dirección, lista de ítems).
     * @return OrderResponse con los datos del pedido creado.
     * @throws RuntimeException si falla la comunicación con el servicio de catálogo.
     */
    public OrderResponse createOrder(OrderRequest request) {
        // Paso 1: Crear la entidad Order con estado inicial PENDING y monto total en cero
        Order order = Order.builder()
                .userId(request.getUserId())
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        // Paso 2: Convertir cada ítem del request en una entidad OrderItem
        // y calcular el subtotal de cada uno (precio unitario * cantidad)
        List<OrderItem> items = request.getItems().stream()
                .map(itemRequest -> {
                    BigDecimal subtotal = itemRequest.getUnitPrice()
                            .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                    return OrderItem.builder()
                            .productId(itemRequest.getProductId())
                            .productName(itemRequest.getProductName())
                            .quantity(itemRequest.getQuantity())
                            .unitPrice(itemRequest.getUnitPrice())
                            .subtotal(subtotal)
                            .order(order) // Asociar cada ítem al pedido padre
                            .build();
                })
                .collect(Collectors.toList());

        // Paso 3: Asignar la lista de ítems al pedido
        order.setItems(items);

        // Paso 4: Calcular el monto total del pedido sumando los subtotales de todos los ítems
        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        // Paso 5: Reducir el stock en el microservicio de catálogo (catalog-service) vía Feign
        try {
            // Construir la lista de solicitudes de reducción de stock
            // Cada elemento contiene el ID del producto y la cantidad a descontar
            List<Map<String, Object>> stockRequests = request.getItems().stream()
                    .map(itemReq -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("productId", itemReq.getProductId());
                        map.put("quantity", itemReq.getQuantity());
                        return map;
                    })
                    .collect(Collectors.toList());
            // Realizar la llamada HTTP PUT al catalog-service
            catalogClient.reduceStock(stockRequests);
            log.info("Stock reduced successfully for order with {} items", stockRequests.size());
        } catch (Exception e) {
            // Si falla la reducción de stock, registrar el error y lanzar excepción
            // para que la transacción haga rollback y no se guarde el pedido
            log.error("Failed to reduce stock in catalog-service: {}", e.getMessage());
            throw new RuntimeException("Could not reduce stock: " + e.getMessage(), e);
        }

        // Paso 6: Guardar el pedido en la base de datos y retornar la respuesta mapeada
        Order savedOrder = orderRepository.save(order);

        // Send notification
        try {
            Map<String, Object> notifRequest = new HashMap<>();
            notifRequest.put("userId", savedOrder.getUserId());
            notifRequest.put("userEmail", request.getUserEmail());
            notifRequest.put("title", "Pedido Creado");
            notifRequest.put("message", "Su pedido ORD-" + savedOrder.getId() + " ha sido creado exitosamente por un total de $" + savedOrder.getTotalAmount());
            notifRequest.put("type", "INFO");
            notificationClient.sendNotification(notifRequest);
            log.info("Notification sent for order {}", savedOrder.getId());
        } catch (Exception e) {
            log.error("Failed to send notification for order {}: {}", savedOrder.getId(), e.getMessage());
        }

        return mapToResponse(savedOrder);
    }

    /**
     * Obtiene un pedido por su ID.
     * Es una operación de solo lectura (readOnly = true), lo que optimiza el rendimiento
     * al no necesitar bloqueos de escritura en la transacción.
     *
     * @param id Identificador del pedido a buscar.
     * @return OrderResponse con los datos del pedido encontrado.
     * @throws EntityNotFoundException si no existe un pedido con el ID proporcionado.
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    /**
     * Obtiene todos los pedidos de un usuario específico.
     * Operación de solo lectura.
     *
     * @param userId ID del usuario cuyos pedidos se desean consultar.
     * @return Lista de OrderResponse con los pedidos del usuario.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el estado de un pedido existente.
     * Busca el pedido por ID, cambia su estado y guarda los cambios en la BD.
     *
     * @param id     Identificador del pedido a actualizar.
     * @param status Nuevo estado del pedido (CONFIRMED, PROCESSING, SHIPPED, etc.).
     * @return OrderResponse con los datos del pedido actualizado.
     * @throws EntityNotFoundException si no existe un pedido con el ID proporcionado.
     */
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    /**
     * Cancela un pedido existente.
     * Regla de negocio: No se puede cancelar un pedido que ya fue entregado (DELIVERED).
     *
     * @param id Identificador del pedido a cancelar.
     * @throws EntityNotFoundException si no existe un pedido con el ID proporcionado.
     * @throws IllegalStateException   si el pedido ya fue entregado.
     */
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        // Validar regla de negocio: no se puede cancelar un pedido ya entregado
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered order");
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Restore stock in catalog-service
        try {
            List<Map<String, Object>> stockRequests = order.getItems().stream()
                    .map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("productId", item.getProductId());
                        map.put("quantity", item.getQuantity());
                        return map;
                    })
                    .collect(Collectors.toList());
            catalogClient.restoreStock(stockRequests);
            log.info("Stock restored successfully for cancelled order with {} items", stockRequests.size());
        } catch (Exception e) {
            log.error("Failed to restore stock in catalog-service for cancelled order: {}", e.getMessage());
            // Depending on business requirements, this might throw an exception or just log the error.
        }
    }

    /**
     * Obtiene todos los pedidos filtrados por un estado específico.
     * Operación de solo lectura.
     *
     * @param status Estado por el cual filtrar los pedidos.
     * @return Lista de OrderResponse con los pedidos que tienen el estado indicado.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---- Métodos privados de mapeo ----

    /**
     * Convierte una entidad Order a su representación DTO (OrderResponse).
     * Mapea los datos del pedido y sus ítems para enviarlos al cliente.
     *
     * @param order Entidad Order obtenida de la base de datos.
     * @return OrderResponse con los datos del pedido formateados para la respuesta HTTP.
     */
    private OrderResponse mapToResponse(Order order) {
        // Mapear cada ítem del pedido a su DTO de respuesta
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());

        // Construir y retornar el DTO de respuesta del pedido completo
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /**
     * Convierte una entidad OrderItem a su representación DTO (OrderItemResponse).
     *
     * @param item Entidad OrderItem obtenida de la base de datos.
     * @return OrderItemResponse con los datos del ítem formateados para la respuesta HTTP.
     */
    private OrderItemResponse mapToItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
