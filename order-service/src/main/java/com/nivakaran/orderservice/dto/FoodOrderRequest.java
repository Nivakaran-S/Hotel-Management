package com.nivakaran.orderservice.dto;

import com.nivakaran.orderservice.model.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FoodOrderRequest(
        @NotNull(message = "Order type is required")
        OrderType orderType,

        String tableId, // Required for DINE_IN

        String roomNumber, // Required for ROOM_SERVICE

        @NotBlank(message = "Guest name is required")
        String guestName,

        @NotBlank(message = "Guest email is required")
        @Email(message = "Invalid email format")
        String guestEmail,

        String guestPhone,

        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemRequest> items,

        String specialInstructions
) {}