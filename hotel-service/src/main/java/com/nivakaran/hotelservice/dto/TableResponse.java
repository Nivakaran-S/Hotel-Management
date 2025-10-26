package com.nivakaran.hotelservice.dto;

import com.nivakaran.hotelservice.model.TableLocation;
import com.nivakaran.hotelservice.model.TableStatus;

import java.math.BigDecimal;

public record TableResponse(
        String id,
        String tableNumber,
        Integer capacity,
        TableLocation location,
        TableStatus status,
        BigDecimal reservationFee,
        Boolean isWindowSeat,
        String description
) {}