package com.nivakaran.paymentservice.dto;

import com.nivakaran.paymentservice.model.PaymentMethod;
import com.nivakaran.paymentservice.model.PaymentStatus;
import com.nivakaran.paymentservice.model.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        String paymentId,
        String bookingNumber,
        PaymentType paymentType,
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String transactionId,
        String guestEmail,
        String guestName,
        String description,
        LocalDateTime paymentDateTime,
        String failureReason,
        String cardLast4Digits,
        String cardType
) {}