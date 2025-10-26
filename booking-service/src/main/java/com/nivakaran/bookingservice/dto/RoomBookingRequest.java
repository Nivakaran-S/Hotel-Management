package com.nivakaran.bookingservice.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RoomBookingRequest(
        @NotBlank(message = "Room ID is required")
        String roomId,

        @NotBlank(message = "Guest name is required")
        String guestName,

        @NotBlank(message = "Guest email is required")
        @Email(message = "Invalid email format")
        String guestEmail,

        @NotBlank(message = "Guest phone is required")
        String guestPhone,

        @NotNull(message = "Check-in date is required")
        @Future(message = "Check-in date must be in the future")
        LocalDate checkInDate,

        @NotNull(message = "Check-out date is required")
        @Future(message = "Check-out date must be in the future")
        LocalDate checkOutDate,

        @NotNull(message = "Number of guests is required")
        @Positive(message = "Number of guests must be positive")
        Integer numberOfGuests,

        String specialRequests
) {}