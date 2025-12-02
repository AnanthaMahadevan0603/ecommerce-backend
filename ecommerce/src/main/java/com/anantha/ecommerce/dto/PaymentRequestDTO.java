package com.anantha.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    @NotNull
    private Long orderId;

    @NotNull
    private BigDecimal amount;

    // simple string for method (CARD/UPI/CASH) — validate in service if needed
    private String method;

    // optional flag to simulate failure (useful in tests). If null or false => success.
    private Boolean simulateFailure;
}
