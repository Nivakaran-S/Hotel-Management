package com.nivakaran.orderservice.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        String menuItemId,
        String menuItemName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        String specialInstructions
) {}