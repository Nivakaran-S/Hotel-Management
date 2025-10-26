package com.nivakaran.paymentservice.service;

import com.nivakaran.paymentservice.dto.BookingValidationResponse;
import com.nivakaran.paymentservice.dto.PaymentGatewayResponse;
import com.nivakaran.paymentservice.dto.PaymentRequest;
import com.nivakaran.paymentservice.dto.PaymentResponse;
import com.nivakaran.paymentservice.exception.BusinessRuleViolationException;
import com.nivakaran.paymentservice.exception.InvalidRequestException;
import com.nivakaran.paymentservice.exception.PaymentException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    // TODO: Inject these clients when implemented
    // private final BookingClient bookingClient;
    // private final OrderClient orderClient;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        String idempotencyKey = UUID.randomUUID().toString();
        return processPayment(request, idempotencyKey);
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request, String idempotencyKey) {
        log.info("Processing payment for booking: {}", request.bookingNumber());

        // 1. Idempotency check
        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            log.info("Payment already processed, returning existing result");
            return mapToResponse(existing.get());
        }

        // 2. Validate booking exists
        BookingValidationResponse booking = validateBooking(request.bookingNumber(),
                request.paymentType());

        // 3. Check for duplicate successful payments
        List<Payment> existingPayments = paymentRepository.findByBookingNumber(request.bookingNumber())
                .stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.SUCCESS)
                .toList();

        if (!existingPayments.isEmpty()) {
            throw new PaymentException("Payment already completed for this booking");
        }

        // 4. Verify amount matches booking
        if (booking.totalAmount().compareTo(request.amount()) != 0) {
            throw new PaymentException(String.format(
                    "Payment amount (%s) does not match booking amount (%s)",
                    request.amount(), booking.totalAmount()));
        }

        // 5. Process payment
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentGatewayResponse gatewayResponse = processPaymentGateway(request);

        // 6. Create payment record
        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .idempotencyKey(idempotencyKey)
                .bookingNumber(request.bookingNumber())
                .paymentType(request.paymentType())
                .amount(request.amount())
                .currency(request.currency())
                .paymentMethod(request.paymentMethod())
                .paymentStatus(gatewayResponse.isSuccess() ?
                        PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .transactionId(gatewayResponse.transactionId())
                .guestEmail(request.guestEmail())
                .guestName(request.guestName())
                .description(request.description())
                .failureReason(gatewayResponse.failureReason())
                .cardLast4Digits(request.cardLast4Digits())
                .cardType(request.cardType())
                .paymentDateTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .processedBy(getCurrentUsername())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // 7. If payment successful, confirm booking
        if (savedPayment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            confirmBookingAfterPayment(request.bookingNumber(), request.paymentType());
        }

        log.info("Payment processed: {} - Status: {}",
                savedPayment.getPaymentId(), savedPayment.getPaymentStatus());

        return mapToResponse(savedPayment);
    }

    // Validate booking exists and get amount
    private BookingValidationResponse validateBooking(String bookingNumber,
                                                      PaymentType paymentType) {
        try {
            // TODO: Replace with actual client calls when implemented
            // Example:
            // return switch (paymentType) {
            //     case ROOM_BOOKING -> bookingClient.validateRoomBooking(bookingNumber);
            //     case TABLE_BOOKING -> bookingClient.validateTableBooking(bookingNumber);
            //     case FOOD_ORDER -> orderClient.validateOrder(bookingNumber);
            //     default -> throw new InvalidRequestException("Invalid payment type");
            // };

            log.warn("Using mock booking validation - implement actual client integration");
            // Mock validation - replace with actual implementation
            return new BookingValidationResponse(
                    bookingNumber,
                    BigDecimal.valueOf(100.00),
                    "USD",
                    true,
                    "CONFIRMED"
            );
        } catch (Exception e) {
            throw new BusinessRuleViolationException(
                    "Booking not found or invalid: " + bookingNumber);
        }
    }

    // Confirm booking after successful payment
    private void confirmBookingAfterPayment(String bookingNumber, PaymentType paymentType) {
        try {
            // TODO: Replace with actual client calls when implemented
            // switch (paymentType) {
            //     case ROOM_BOOKING -> bookingClient.confirmRoomBooking(bookingNumber);
            //     case TABLE_BOOKING -> bookingClient.confirmTableBooking(bookingNumber);
            //     case FOOD_ORDER -> orderClient.confirmOrder(bookingNumber);
            // }

            log.info("Booking confirmed after payment: {}", bookingNumber);
        } catch (Exception e) {
            log.error("Failed to confirm booking after payment: {}", e.getMessage());
            // This is critical - might need compensating transaction
        }
    }

    // Simulate payment gateway processing
    private PaymentGatewayResponse processPaymentGateway(PaymentRequest request) {
        // Simulate payment gateway processing
        // In production, integrate with real payment gateway (Stripe, PayPal, etc.)
        log.info("Processing payment gateway for amount: {} {}", request.amount(), request.currency());

        // 95% success rate simulation
        boolean success = Math.random() > 0.05;

        if (success) {
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            return new PaymentGatewayResponse(
                    true,
                    transactionId,
                    null,
                    "GATEWAY-REF-" + System.currentTimeMillis()
            );
        } else {
            return new PaymentGatewayResponse(
                    false,
                    null,
                    "Payment declined by gateway",
                    null
            );
        }
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
                .orElseThrow(() -> new PaymentException("Payment not found with id: " + id));
        return mapToResponse(payment);
    }

    public PaymentResponse getPaymentByPaymentId(String paymentId) {
        log.info("Fetching payment by paymentId: {}", paymentId);
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found with paymentId: " + paymentId));
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
                .orElseThrow(() -> new PaymentException("Payment not found with paymentId: " + paymentId));

        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Only successful payments can be refunded");
        }

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());
        payment.setProcessedBy(getCurrentUsername());

        Payment refundedPayment = paymentRepository.save(payment);
        log.info("Payment refunded successfully: {}", paymentId);

        return mapToResponse(refundedPayment);
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