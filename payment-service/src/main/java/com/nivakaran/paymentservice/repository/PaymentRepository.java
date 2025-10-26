package com.nivakaran.paymentservice.repository;

import com.nivakaran.paymentservice.model.Payment;
import com.nivakaran.paymentservice.model.PaymentStatus;
import com.nivakaran.paymentservice.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    List<Payment> findByBookingNumber(String bookingNumber);
    List<Payment> findByGuestEmail(String guestEmail);
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Payment> findByPaymentType(PaymentType paymentType);
    Optional<Payment> findByTransactionId(String transactionId);
}