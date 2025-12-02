package com.anantha.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    @NotNull
    private Long userId;

    // Optional: shipping address, payment reference, etc. Add fields later as needed.
    private String shippingAddress;
}
