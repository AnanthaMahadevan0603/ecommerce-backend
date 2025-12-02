package com.anantha.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartItemDTO {
    @NotNull
    private Long itemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
