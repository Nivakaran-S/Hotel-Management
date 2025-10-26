package com.nivakaran.hotelservice.dto;

import com.nivakaran.hotelservice.model.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RoomRequest(
        @NotBlank(message = "Room number is required")
        String roomNumber,

        @NotNull(message = "Room type is required")
        RoomType roomType,

        @NotNull(message = "Price per night is required")
        @Positive(message = "Price must be positive")
        BigDecimal pricePerNight,

        @NotNull(message = "Capacity is required")
        @Positive(message = "Capacity must be positive")
        Integer capacity,

        String description,
        String floor,
        Boolean hasBalcony,
        Boolean hasSeaView,
        String amenities
) {}