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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;

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
