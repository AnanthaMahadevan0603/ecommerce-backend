package com.anantha.ecommerce.controller;

import com.anantha.ecommerce.dto.PaymentRequestDTO;
import com.anantha.ecommerce.dto.PaymentResponseDTO;
import com.anantha.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Simple payment endpoints:
 * POST /api/payments/process  -> processes a payment (dummy)
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO dto) {
        PaymentResponseDTO resp = paymentService.processPayment(dto);
        return ResponseEntity.ok(resp);
    }
}
