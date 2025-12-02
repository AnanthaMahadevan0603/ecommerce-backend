package com.anantha.ecommerce.service;

import com.anantha.ecommerce.dto.AddToCartDTO;
import com.anantha.ecommerce.dto.CartDTO;
import com.anantha.ecommerce.dto.UpdateCartItemDTO;

public interface CartService {

    CartDTO getCartByUserId(Long userId);

    CartDTO addToCart(AddToCartDTO dto);

    CartDTO updateCartItemQuantity(UpdateCartItemDTO dto);

    void removeCartItem(Long itemId);

    CartDTO clearCart(Long userId);
}
