package com.anantha.ecommerce.service;

import com.anantha.ecommerce.dto.PaymentRequestDTO;
import com.anantha.ecommerce.dto.PaymentResponseDTO;

public interface PaymentService {

    /**
     * Process payment for the given order. On success, marks the corresponding Order as PAID.
     */
    PaymentResponseDTO processPayment(PaymentRequestDTO request);
}
