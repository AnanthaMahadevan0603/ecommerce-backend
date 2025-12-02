package com.anantha.ecommerce.service;

import com.anantha.ecommerce.dto.OrderRequestDTO;
import com.anantha.ecommerce.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    /**
     * Place an order for given userId by transferring items from cart to order.
     * Returns created OrderResponseDTO.
     */
    OrderResponseDTO placeOrder(OrderRequestDTO dto);

    /**
     * Get order by id (admin or owner can call).
     */
    OrderResponseDTO getOrderById(Long orderId);

    /**
     * Get all orders placed by a user (order history).
     */
    List<OrderResponseDTO> getOrdersByUser(Long userId);

    /**
     * Admin: get all orders
     */
    List<OrderResponseDTO> getAllOrders();

    /**
     * Update order status (e.g., mark as SHIPPED, CANCELLED). Admin only.
     */
    OrderResponseDTO updateOrderStatus(Long orderId, String status);
}
