package com.anantha.ecommerce.controller;

import com.anantha.ecommerce.dto.*;
import com.anantha.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Get user's cart
    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    // Add to cart
    @PostMapping("/add")
    public ResponseEntity<CartDTO> addToCart(@Valid @RequestBody AddToCartDTO dto) {
        CartDTO updated = cartService.addToCart(dto);
        return ResponseEntity.ok(updated);
    }

    // Update quantity of an existing cart item
    @PutMapping("/item")
    public ResponseEntity<CartDTO> updateItem(@Valid @RequestBody UpdateCartItemDTO dto) {
        CartDTO updated = cartService.updateCartItemQuantity(dto);
        return ResponseEntity.ok(updated);
    }

    // Remove item
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        cartService.removeCartItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // Clear cart
    @PostMapping("/clear/{userId}")
    public ResponseEntity<CartDTO> clearCart(@PathVariable Long userId) {
        CartDTO cleared = cartService.clearCart(userId);
        return ResponseEntity.ok(cleared);
    }
}
