package com.nivakaran.bookingservice.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record TableBookingRequest(
        @NotBlank(message = "Table ID is required")
        String tableId,

        @NotBlank(message = "Guest name is required")
        String guestName,

        @NotBlank(message = "Guest email is required")
        @Email(message = "Invalid email format")
        String guestEmail,

        @NotBlank(message = "Guest phone is required")
        String guestPhone,

        @NotNull(message = "Reservation date is required")
        @FutureOrPresent(message = "Reservation date must be today or in the future")
        LocalDate reservationDate,

        @NotNull(message = "Reservation time is required")
        LocalTime reservationTime,

        @NotNull(message = "Number of guests is required")
        @Positive(message = "Number of guests must be positive")
        Integer numberOfGuests,

        Integer durationMinutes,
        String specialRequests
) {}