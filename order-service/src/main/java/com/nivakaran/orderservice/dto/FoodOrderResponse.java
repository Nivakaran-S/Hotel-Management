package com.nivakaran.orderservice.dto;

import com.nivakaran.orderservice.model.OrderStatus;
import com.nivakaran.orderservice.model.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record FoodOrderResponse(
        Long id,
        String orderNumber,
        OrderType orderType,
        String tableId,
        String roomNumber,
        String guestName,
        String guestEmail,
        String guestPhone,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal serviceCharge,
        BigDecimal totalAmount,
        OrderStatus status,
        String specialInstructions,
        LocalDateTime orderDateTime,
        LocalDateTime estimatedDeliveryTime,
        LocalDateTime actualDeliveryTime,
        List<OrderItemResponse> items
) {}