package com.anantha.ecommerce.dto;

import com.anantha.ecommerce.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String method;
    private PaymentStatus status;
    private String providerReference;
    private Instant createdAt;
    private String message;
}
