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

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final NotificationClient notificationClient;

    public OrderResponse createOrder(OrderRequest request) {
        Order order = Order.builder()
                .userId(request.getUserId())
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

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
                            .order(order)
                            .build();
                })
                .collect(Collectors.toList());

        order.setItems(items);

        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        // Reduce stock in catalog-service
        try {
            List<Map<String, Object>> stockRequests = request.getItems().stream()
                    .map(itemReq -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("productId", itemReq.getProductId());
                        map.put("quantity", itemReq.getQuantity());
                        return map;
                    })
                    .collect(Collectors.toList());
            catalogClient.reduceStock(stockRequests);
            log.info("Stock reduced successfully for order with {} items", stockRequests.size());
        } catch (Exception e) {
            log.error("Failed to reduce stock in catalog-service: {}", e.getMessage());
            throw new RuntimeException("Could not reduce stock: " + e.getMessage(), e);
        }

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

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
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

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ---- Private mapping methods ----

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());

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
