package com.anantha.ecommerce.mapper;

import com.anantha.ecommerce.dto.PaymentResponseDTO;
import com.anantha.ecommerce.entity.Payment;

public class PaymentMapper {

    public static PaymentResponseDTO toDTO(Payment p, String message) {
        if (p == null) return null;

        return PaymentResponseDTO.builder()
                .id(p.getId())
                .orderId(p.getOrder() != null ? p.getOrder().getId() : null)
                .amount(p.getAmount())
                .method(p.getMethod())
                .status(p.getStatus())
                .providerReference(p.getProviderReference())
                .createdAt(p.getCreatedAt())
                .message(message)
                .build();
    }
}
