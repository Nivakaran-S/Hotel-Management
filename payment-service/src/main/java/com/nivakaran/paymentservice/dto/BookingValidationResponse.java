package com.nivakaran.paymentservice.dto;

import java.math.BigDecimal;

public record BookingValidationResponse(
        String bookingNumber,
        BigDecimal totalAmount,
        String currency,
        boolean isValid,
        String status
) {
}