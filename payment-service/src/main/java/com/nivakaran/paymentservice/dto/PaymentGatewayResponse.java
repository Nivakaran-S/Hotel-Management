package com.nivakaran.paymentservice.dto;

public record PaymentGatewayResponse(
        boolean isSuccess,
        String transactionId,
        String failureReason,
        String gatewayReference
) {
}