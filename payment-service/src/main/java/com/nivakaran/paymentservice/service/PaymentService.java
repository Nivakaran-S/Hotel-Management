package com.nivakaran.paymentservice.service;

import com.nivakaran.paymentservice.dto.PaymentRequest;
import com.nivakaran.paymentservice.dto.PaymentResponse;
import com.nivakaran.paymentservice.model.Payment;
import com.nivakaran.paymentservice.model.PaymentStatus;
import com.nivakaran.paymentservice.model.PaymentType;
import com.nivakaran.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for booking: {}", request.bookingNumber());

        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String processedBy = getCurrentUsername();

        // Simulate payment processing
        boolean paymentSuccess = simulatePaymentGateway(request);

        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .bookingNumber(request.bookingNumber())
                .paymentType(request.paymentType())
                .amount(request.amount())
                .currency(request.currency())
                .paymentMethod(request.paymentMethod())
                .paymentStatus(paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .transactionId(paymentSuccess ? "TXN-" + UUID.randomUUID().toString() : null)
                .guestEmail(request.guestEmail())
                .guestName(request.guestName())
                .description(request.description())
                .paymentDateTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .processedBy(processedBy)
                .failureReason(paymentSuccess ? null : "Payment declined by gateway")
                .cardLast4Digits(request.cardLast4Digits())
                .cardType(request.cardType())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed: {} - Status: {}", savedPayment.getPaymentId(), savedPayment.getPaymentStatus());

        return mapToResponse(savedPayment);
    }

    public List<PaymentResponse> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse getPaymentById(Long id) {
        log.info("Fetching payment by id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return mapToResponse(payment);
    }

    public PaymentResponse getPaymentByPaymentId(String paymentId) {
        log.info("Fetching payment by paymentId: {}", paymentId);
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with paymentId: " + paymentId));
        return mapToResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByBookingNumber(String bookingNumber) {
        log.info("Fetching payments for booking: {}", bookingNumber);
        return paymentRepository.findByBookingNumber(bookingNumber).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByGuestEmail(String email) {
        log.info("Fetching payments for guest: {}", email);
        return paymentRepository.findByGuestEmail(email).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        log.info("Fetching payments by status: {}", status);
        return paymentRepository.findByPaymentStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getPaymentsByType(PaymentType type) {
        log.info("Fetching payments by type: {}", type);
        return paymentRepository.findByPaymentType(type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse refundPayment(String paymentId) {
        log.info("Processing refund for payment: {}", paymentId);

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with paymentId: " + paymentId));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new RuntimeException("Only successful payments can be refunded");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment refundedPayment = paymentRepository.save(payment);
        log.info("Payment refunded successfully: {}", paymentId);

        return mapToResponse(refundedPayment);
    }

    private boolean simulatePaymentGateway(PaymentRequest request) {
        // Simulate payment gateway processing
        // In production, integrate with real payment gateway (Stripe, PayPal, etc.)
        log.info("Simulating payment gateway for amount: {} {}", request.amount(), request.currency());

        // 95% success rate simulation
        return Math.random() > 0.05;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentId(),
                payment.getBookingNumber(),
                payment.getPaymentType(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getTransactionId(),
                payment.getGuestEmail(),
                payment.getGuestName(),
                payment.getDescription(),
                payment.getPaymentDateTime(),
                payment.getFailureReason(),
                payment.getCardLast4Digits(),
                payment.getCardType()
        );
    }
}