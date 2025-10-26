package com.nivakaran.hotelservice.dto;

import com.nivakaran.hotelservice.model.TableLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TableRequest(
        @NotBlank(message = "Table number is required")
        String tableNumber,

        @NotNull(message = "Capacity is required")
        @Positive(message = "Capacity must be positive")
        Integer capacity,

        @NotNull(message = "Location is required")
        TableLocation location,

        BigDecimal reservationFee,
        Boolean isWindowSeat,
        String description
) {}