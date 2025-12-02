package com.anantha.ecommerce.mapper;

import com.anantha.ecommerce.dto.CartDTO;
import com.anantha.ecommerce.dto.CartItemDTO;
import com.anantha.ecommerce.entity.Cart;
import com.anantha.ecommerce.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartDTO toDTO(Cart cart) {
        if (cart == null) return null;

        List<CartItemDTO> items = cart.getItems().stream()
                .map(CartMapper::toItemDTO)
                .collect(Collectors.toList());

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .items(items)
                .totalPrice(cart.getTotalPrice() != null ? cart.getTotalPrice() : BigDecimal.ZERO)
                .createdAt(cart.getCreatedAt())
                .build();
    }

    public static CartItemDTO toItemDTO(CartItem ci) {
        if (ci == null) return null;

        BigDecimal total = ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));

        return CartItemDTO.builder()
                .id(ci.getId())
                .productId(ci.getProduct().getId())
                .productName(ci.getProduct().getName())
                .quantity(ci.getQuantity())
                .unitPrice(ci.getUnitPrice())
                .subTotal(total)
                .build();
    }
}
