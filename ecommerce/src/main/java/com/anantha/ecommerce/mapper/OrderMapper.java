package com.anantha.ecommerce.mapper;

import com.anantha.ecommerce.dto.OrderItemDTO;
import com.anantha.ecommerce.dto.OrderResponseDTO;
import com.anantha.ecommerce.entity.Order;
import com.anantha.ecommerce.entity.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponseDTO toDTO(Order order) {
        if (order == null) return null;

        List<OrderItemDTO> items = order.getItems().stream()
                .map(OrderMapper::toItemDTO)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }

    public static OrderItemDTO toItemDTO(OrderItem oi) {
        if (oi == null) return null;

        return OrderItemDTO.builder()
                .id(oi.getId())
                .productId(oi.getProductId())
                .productName(oi.getProductName())
                .quantity(oi.getQuantity())
                .unitPrice(oi.getUnitPrice())
                .subTotal(oi.getSubTotal())
                .build();
    }
}
