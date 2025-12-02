package com.anantha.ecommerce.service.impl;

import com.anantha.ecommerce.dto.PaymentRequestDTO;
import com.anantha.ecommerce.dto.PaymentResponseDTO;
import com.anantha.ecommerce.entity.*;
import com.anantha.ecommerce.repository.OrderRepository;
import com.anantha.ecommerce.repository.PaymentRepository;
import com.anantha.ecommerce.service.CartService;
import com.anantha.ecommerce.service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService; // optional, not used here but available if needed

    /**
     * Dummy payment implementation:
     * - If simulateFailure == Boolean.TRUE => fail payment
     * - Otherwise, treat as success, mark order as PAID and create Payment record with SUCCESS
     */
    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {

        // Validate order exists
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Validate amount matches order total (basic check)
        BigDecimal orderAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        if (request.getAmount() == null || request.getAmount().compareTo(orderAmount) != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment amount must match order total");
        }

        // Create initial payment record (INITIATED)
        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .method(request.getMethod() != null ? request.getMethod() : "UNKNOWN")
                .status(PaymentStatus.INITIATED)
                .providerReference(null)
                .createdAt(Instant.now())
                .build();

        payment = paymentRepository.save(payment);

        // Simulate gateway behavior
        boolean simulateFail = request.getSimulateFailure() != null && request.getSimulateFailure();

        if (simulateFail) {
            // Mark payment as FAILED
            payment.setStatus(PaymentStatus.FAILED);
            payment.setProviderReference("SIM_FAIL_" + UUID.randomUUID());
            paymentRepository.save(payment);

            // Do not change order status (remains PENDING)
            return mapToDto(payment, "Payment failed (simulated)");
        }

        // Simulate success path
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setProviderReference("SIM_SUCC_" + UUID.randomUUID());
        paymentRepository.save(payment);

        // Mark order as PAID
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // Optionally: trigger invoice, notification, etc. (not implemented here)

        return mapToDto(payment, "Payment successful");
    }

    private PaymentResponseDTO mapToDto(Payment payment, String message) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .providerReference(payment.getProviderReference())
                .createdAt(payment.getCreatedAt())
                .message(message)
                .build();
    }
}
