package com.nivakaran.bookingservice.dto;

import com.nivakaran.bookingservice.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TableBookingResponse(
        Long id,
        String bookingNumber,
        String tableId,
        String guestName,
        String guestEmail,
        String guestPhone,
        LocalDate reservationDate,
        LocalTime reservationTime,
        Integer numberOfGuests,
        Integer durationMinutes,
        BigDecimal reservationFee,
        BookingStatus status,
        String specialRequests,
        LocalDateTime bookingDateTime
) {}