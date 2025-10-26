package com.nivakaran.paymentservice.dto;

import com.nivakaran.paymentservice.model.PaymentMethod;
import com.nivakaran.paymentservice.model.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank(message = "Booking number is required")
        String bookingNumber,

        @NotNull(message = "Payment type is required")
        PaymentType paymentType,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Currency is required")
        String currency,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotBlank(message = "Guest email is required")
        String guestEmail,

        @NotBlank(message = "Guest name is required")
        String guestName,

        String description,

        String cardLast4Digits,

        String cardType
) {}