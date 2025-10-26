package com.nivakaran.hotelservice.dto;

import com.nivakaran.hotelservice.model.RoomStatus;
import com.nivakaran.hotelservice.model.RoomType;

import java.math.BigDecimal;

public record RoomResponse(
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
        String amenities
) {}