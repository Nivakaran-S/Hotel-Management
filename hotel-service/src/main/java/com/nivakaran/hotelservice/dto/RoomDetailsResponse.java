package com.nivakaran.hotelservice.dto;

import com.nivakaran.hotelservice.model.RoomStatus;
import com.nivakaran.hotelservice.model.RoomType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RoomDetailsResponse(
        String id,
        String roomNumber,
        RoomType roomType,
        BigDecimal pricePerNight,
        Integer capacity,
        RoomStatus status,
        String description,
        String floor,
        Boolean hasBalcony,
        Boolean hasSeaView,
        String amenities,
        Boolean isAvailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdBy
) {
}
