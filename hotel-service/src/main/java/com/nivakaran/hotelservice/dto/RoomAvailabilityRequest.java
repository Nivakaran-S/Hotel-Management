package com.nivakaran.hotelservice.dto;

import com.nivakaran.hotelservice.model.RoomType;

import java.time.LocalDate;

public record RoomAvailabilityRequest(
        RoomType roomType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer guestCount
) {}