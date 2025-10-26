package com.nivakaran.bookingservice.dto;

import java.math.BigDecimal;

public record RoomResponse(
        String id,
        String roomNumber,
        String roomType,
        BigDecimal pricePerNight,
        String status,
        Integer capacity,
        String description
) {}