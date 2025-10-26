package com.nivakaran.bookingservice.dto;

import com.nivakaran.bookingservice.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RoomBookingResponse(
        Long id,
        String bookingNumber,
        String roomId,
        String guestName,
        String guestEmail,
        String guestPhone,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer numberOfGuests,
        Integer numberOfNights,
        BigDecimal roomPrice,
        BigDecimal totalAmount,
        BookingStatus status,
        String specialRequests,
        LocalDateTime bookingDateTime
) {}