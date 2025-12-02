package com.anantha.ecommerce.service.impl;

import com.anantha.ecommerce.dto.*;
import com.anantha.ecommerce.entity.*;
import com.anantha.ecommerce.repository.*;
import com.anantha.ecommerce.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public CartDTO getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createEmptyCartForUser(user));

        return mapToDto(cart);
    }

    @Override
    @Transactional
    public CartDTO addToCart(AddToCartDTO dto) {
        // validate user & product
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getStock() == null || product.getStock() < dto.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient product stock");
        }

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createEmptyCartForUser(user));

        // check if item exists in cart -> update quantity
        CartItem existingItem = cart.getItems().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            existingItem.setUnitPrice(product.getPrice());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            cart.getItems().add(newItem);
            // cascade persist through cart.save later or save explicitly
            cartItemRepository.save(newItem);
        }

        // update cart total
        recalculateCartTotal(cart);
        cartRepository.save(cart);

        return mapToDto(cart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItemQuantity(UpdateCartItemDTO dto) {
        CartItem item = cartItemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        Product product = item.getProduct();
        if (product.getStock() == null || product.getStock() < dto.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient product stock");
        }

        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(product.getPrice());
        cartItemRepository.save(item);

        Cart cart = item.getCart();
        recalculateCartTotal(cart);
        cartRepository.save(cart);

        return mapToDto(cart);
    }

    @Override
    @Transactional
    public void removeCartItem(Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
        Cart cart = item.getCart();
        cart.getItems().removeIf(ci -> ci.getId().equals(itemId));
        cartItemRepository.delete(item);
        recalculateCartTotal(cart);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartDTO clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> createEmptyCartForUser(user));
        // remove items
        List<CartItem> itemsToRemove = List.copyOf(cart.getItems());
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
        // delete items individually to keep DB clean
        itemsToRemove.forEach(ci -> {
            if (ci.getId() != null) {
                cartItemRepository.deleteById(ci.getId());
            }
        });
        return mapToDto(cart);
    }

    // ---------- helpers ----------

    private Cart createEmptyCartForUser(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .createdAt(Instant.now())
                .totalPrice(BigDecimal.ZERO)
                .build();
        return cartRepository.save(cart);
    }

    private void recalculateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(ci -> ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    private CartDTO mapToDto(Cart cart) {
        List<CartItemDTO> itemDTOs = cart.getItems().stream().map(ci -> {
            BigDecimal sub = ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            return CartItemDTO.builder()
                    .id(ci.getId())
                    .productId(ci.getProduct().getId())
                    .productName(ci.getProduct().getName())
                    .quantity(ci.getQuantity())
                    .unitPrice(ci.getUnitPrice())
                    .subTotal(sub)
                    .build();
        }).collect(Collectors.toList());

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .items(itemDTOs)
                .totalPrice(cart.getTotalPrice())
                .createdAt(cart.getCreatedAt())
                .build();
    }
}
