package com.nivakaran.paymentservice.controller;

import com.nivakaran.paymentservice.dto.PaymentRequest;
import com.nivakaran.paymentservice.dto.PaymentResponse;
import com.nivakaran.paymentservice.model.PaymentStatus;
import com.nivakaran.paymentservice.model.PaymentType;
import com.nivakaran.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('customer') or hasRole('staff') or hasRole('admin')")
    public PaymentResponse processPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public PaymentResponse getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/payment-id/{paymentId}")
    public PaymentResponse getPaymentByPaymentId(@PathVariable String paymentId) {
        return paymentService.getPaymentByPaymentId(paymentId);
    }

    @GetMapping("/booking/{bookingNumber}")
    public List<PaymentResponse> getPaymentsByBookingNumber(@PathVariable String bookingNumber) {
        return paymentService.getPaymentsByBookingNumber(bookingNumber);
    }

    @GetMapping("/guest/{email}")
    public List<PaymentResponse> getPaymentsByGuestEmail(@PathVariable String email) {
        return paymentService.getPaymentsByGuestEmail(email);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<PaymentResponse> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return paymentService.getPaymentsByStatus(status);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public List<PaymentResponse> getPaymentsByType(@PathVariable PaymentType type) {
        return paymentService.getPaymentsByType(type);
    }

    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasRole('staff') or hasRole('admin')")
    public PaymentResponse refundPayment(@PathVariable String paymentId) {
        return paymentService.refundPayment(paymentId);
    }
}